package br.com.wedding_site_backend.service;

import br.com.wedding_site_backend.domain.PresenteRecebido;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.noivos}")
    private String emailNoivos;

    @Value("${spring.mail.username}")
    private String emailRemetente;

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

    // ── Envio ─────────────────────────────────────────────
    private void enviar(String para, String assunto, String html)
            throws MessagingException {
        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper h = new MimeMessageHelper(msg, true, "UTF-8");
        try {
            h.setFrom(emailRemetente, "Taís & Gabriel 🦋");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        h.setTo(para);
        h.setSubject(assunto);
        h.setText(html, true);
        mailSender.send(msg);
    }

    // ── Templates ─────────────────────────────────────────
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
              .row{display:flex;justify-content:space-between;padding:.35rem 0;
                   border-bottom:1px solid rgba(146,168,209,.3);font-size:.87rem}
              .row:last-child{border-bottom:none;font-weight:700}
              .label{color:#8A7A9A}.value{color:#2C2035}
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
                Seu presente foi confirmado. Obrigado por fazer parte do nosso dia especial — seu carinho significa tudo para nós. 💜</p>
                <div class="box">
                  <div class="row"><span class="label">Pagamento</span><span class="value">%s</span></div>
                  <div class="row"><span class="label">Data</span><span class="value">%s</span></div>
                  <div class="row"><span class="label">Valor</span><span class="value">%s</span></div>
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
              .row{display:flex;justify-content:space-between;font-size:.87rem;
                   padding:.35rem 0;border-bottom:1px solid rgba(146,168,209,.25)}
              .row:last-child{border-bottom:none;font-weight:700;color:#5E7FA8}
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
                  <div class="row"><span>Nome</span><span>%s</span></div>
                  <div class="row"><span>Email</span><span>%s</span></div>
                  <div class="row"><span>Pagamento</span><span>%s</span></div>
                  <div class="row"><span>Data</span><span>%s</span></div>
                  <div class="row"><span>Valor</span><span>%s</span></div>
                </div>
                %s
              </div>
            </div></body></html>
            """.formatted(nome, nome, email, pagLabel, data, valor, msgBox);
    }
}