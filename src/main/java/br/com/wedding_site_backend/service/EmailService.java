package br.com.wedding_site_backend.service;

import br.com.wedding_site_backend.domain.PresenteRecebido;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    // RestTemplate simples e síncrono — já vem disponível via spring-boot-starter-web,
    // não precisa de nenhuma dependência nova no pom.xml.
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${sendgrid.api-key}")
    private String sendgridApiKey;

    @Value("${app.email.noivos}")
    private String emailNoivos;

    // Precisa ser EXATAMENTE o endereço verificado via Single Sender Verification
    // no painel do SendGrid (Settings -> Sender Authentication -> Single Sender Verification).
    @Value("${sendgrid.remetente-email}")
    private String remetenteEmail;

    @Value("${sendgrid.remetente-nome:Tais & Gabriel}")
    private String remetenteNome;

    private static final String SENDGRID_API_URL = "https://api.sendgrid.com/v3/mail/send";

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    private static final NumberFormat BRL =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    // ── Email para o convidado ────────────────────────────
    @Async
    public void enviarConfirmacaoConvidado(String nome, String email,
                                           PresenteRecebido c, String forma) {
        try {
            String html = templateConvidado(nome, c, forma);
            enviar(email, "💜 Presente confirmado — Casamento Taís & Gabriel", html);
            log.info("Confirmação enviada para {}", email);
        } catch (Exception e) {
            log.error("Erro ao enviar email para {}: {}", email, e.getMessage());
        }
    }

    // ── Email para os noivos ──────────────────────────────
    @Async
    public void enviarNotificacaoNoivos(String nomeDoador, String emailDoador,
                                        String mensagem, PresenteRecebido c, String forma) {
        try {
            String html = templateNoivos(nomeDoador, emailDoador, mensagem, c, forma);
            enviar(emailNoivos, "🎁 Novo presente de " + nomeDoador, html);
            log.info("Notificação enviada aos noivos sobre presente de {}", nomeDoador);
        } catch (Exception e) {
            log.error("Erro ao notificar noivos: {}", e.getMessage());
        }
    }

    // ── Envio via SendGrid (HTTPS API, funciona em qualquer plano do Railway) ──
    private void enviar(String para, String assunto, String html) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(sendgridApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "personalizations", List.of(
                        Map.of("to", List.of(Map.of("email", para)))
                ),
                "from", Map.of(
                        "email", remetenteEmail,
                        "name", remetenteNome
                ),
                "subject", assunto,
                "content", List.of(
                        Map.of("type", "text/html", "value", html)
                )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(SENDGRID_API_URL, request, String.class);
    }

    // ── Templates ─────────────────────────────────────────
    // Nota: as linhas "label / valor" usam <table> em vez de flexbox (display:flex).
    // Isso é necessário porque muitos clientes de email (Gmail, Outlook, apps móveis)
    // têm suporte parcial ou nenhum a flexbox/justify-content, o que fazia o texto
    // aparecer colado ("Nome:tatah"). Tabelas têm suporte universal em email HTML.
    private String templateConvidado(String nome, PresenteRecebido c, String forma) {
        String valor    = BRL.format(c.getTotal());
        String data     = c.getCriadoEm() != null ? c.getCriadoEm().format(FMT) : "agora";
        String pagLabel = "PIX".equals(forma) ? "📱 Pix" : "💳 Cartão de Crédito";
        String msgBox   = (c.getMensagem() != null && !c.getMensagem().isBlank())
                ? "<div class=\"msg-box\">Sua mensagem para os noivos:<br><em>\"" + c.getMensagem() + "\"</em></div>"
                : "";

        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head><meta charset="UTF-8">
                <meta name="viewport" content="width=device-width,initial-scale=1">
                <style>
                  body{margin:0;font-family:Arial,sans-serif;background:#FAF7F2;color:#2C2035}
                  .wrap{max-width:560px;margin:0 auto;padding:1.5rem 1rem}
                  .header{background:linear-gradient(135deg,#5E7FA8,#9A6EB0);border-radius:16px 16px 0 0;padding:2.5rem;text-align:center}
                  .header h1{font-family:Georgia,serif;color:white;font-size:2rem;margin:0 0 .3rem;font-style:italic}
                  .header p{color:rgba(255,255,255,.85);margin:0;font-size:.9rem;letter-spacing:.15em}
                  .body{background:white;border-radius:0 0 16px 16px;padding:2rem}
                  .greeting{font-size:1rem;color:#5A4E6A;margin-bottom:1.5rem;line-height:1.7}
                  .box{background:#DCE8F5;border-radius:12px;padding:1.2rem 1.5rem;margin-bottom:1.2rem}
                  .row-table{width:100%%;border-collapse:collapse}
                  .row-table td{padding:.5rem 0;border-bottom:1px solid rgba(146,168,209,.3);font-size:.87rem}
                  .row-table tr:last-child td{border-bottom:none;font-weight:700}
                  .label{color:#8A7A9A;text-align:left}
                  .value{color:#2C2035;text-align:right}
                  .msg-box{background:#F3EAF7;border-left:4px solid #9A6EB0;border-radius:0 8px 8px 0;
                           padding:1rem 1.2rem;margin-bottom:1.2rem;font-style:italic;color:#5A4E6A;font-size:.9rem}
                  .footer{text-align:center;margin-top:2rem;font-size:.8rem;color:#8A7A9A}
                  .footer strong{color:#5E7FA8}
                </style>
                </head>
                <body><div class="wrap">
                  <div class="header">
                    <div style="font-size:2rem;margin-bottom:.5rem">🦋</div>
                    <h1>Presente Confirmado!</h1>
                    <p>Taís &amp; Gabriel · 19.09.2026</p>
                  </div>
                  <div class="body">
                    <p class="greeting">Olá, <strong>%s</strong>!<br><br>
                        Seu presente foi confirmado! ✨<br>
                        Obrigada por fazer parte desse momento tão especial das nossas vidas.<br>
                        Cada gesto de carinho torna essa jornada ainda mais inesquecível para nós. 🩵
                    </p>
                    <div class="box">
                      <table class="row-table" role="presentation" cellpadding="0" cellspacing="0">
                        <tr><td class="label">Pagamento</td><td class="value">%s</td></tr>
                        <tr><td class="label">Data</td><td class="value">%s</td></tr>
                        <tr><td class="label">Valor</td><td class="value">%s</td></tr>
                      </table>
                    </div>
                    %s
                    <p class="greeting" style="font-size:.9rem">
                      ✦ Nos vemos em <strong>19/09/2026 às 16h</strong> no
                      <strong>Restaurante Natureza Viva</strong> — Feira Nova, PE. 🦋
                    </p>
                  </div>
                  <div class="footer">Com amor, <strong>Taís &amp; Gabriel</strong></div>
                </div></body></html>
                """.formatted(nome, pagLabel, data, valor, msgBox);
    }

    private String templateNoivos(String nome, String email, String mensagem,
                                  PresenteRecebido c, String forma) {
        String valor    = BRL.format(c.getTotal());
        String data     = c.getCriadoEm() != null ? c.getCriadoEm().format(FMT) : "agora";
        String pagLabel = "PIX".equals(forma) ? "📱 Pix" : "💳 Cartão";
        String msgBox   = (mensagem != null && !mensagem.isBlank())
                ? "<div class=\"msg-box\">💌 <em>\"" + mensagem + "\"</em></div>"
                : "";

        return """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head><meta charset="UTF-8">
            <meta name="viewport" content="width=device-width,initial-scale=1">
            <style>
              body{margin:0;font-family:Arial,sans-serif;background:#FAF7F2;color:#2C2035}
              .wrap{max-width:520px;margin:0 auto;padding:1.5rem 1rem}
              .header{background:linear-gradient(135deg,#9A6EB0,#5E7FA8);border-radius:16px 16px 0 0;
                      padding:2rem;text-align:center;color:white}
              .header h1{font-size:1.5rem;margin:0;font-style:italic}
              .body{background:white;border-radius:0 0 16px 16px;padding:1.8rem}
              .box{background:#DCE8F5;border-radius:10px;padding:1.2rem;margin:1rem 0}
              .row-table{width:100%%;border-collapse:collapse}
              .row-table td{padding:.4rem 0;font-size:.87rem;border-bottom:1px solid rgba(146,168,209,.25)}
              .row-table tr:last-child td{border-bottom:none;font-weight:700;color:#5E7FA8}
              .label{text-align:left}
              .value{text-align:right}
              .msg-box{background:#F3EAF7;border-left:4px solid #9A6EB0;padding:.8rem 1rem;
                       border-radius:0 8px 8px 0;font-style:italic;font-size:.88rem;color:#5A4E6A;margin-top:1rem}
            </style>
            </head>
            <body><div class="wrap">
              <div class="header">
                <div style="font-size:2rem;margin-bottom:.5rem">🎁</div>
                <h1>Novo Presente Recebido!</h1>
              </div>
              <div class="body">
                <p>Vocês receberam um presente de <strong>%s</strong>!</p>
                <div class="box">
                  <table class="row-table" role="presentation" cellpadding="0" cellspacing="0">
                    <tr><td class="label">Nome</td><td class="value">%s</td></tr>
                    <tr><td class="label">Email</td><td class="value">%s</td></tr>
                    <tr><td class="label">Pagamento</td><td class="value">%s</td></tr>
                    <tr><td class="label">Data</td><td class="value">%s</td></tr>
                    <tr><td class="label">Valor</td><td class="value">%s</td></tr>
                  </table>
                </div>
                %s
              </div>
            </div></body></html>
            """.formatted(nome, nome, email, pagLabel, data, valor, msgBox);
    }
}