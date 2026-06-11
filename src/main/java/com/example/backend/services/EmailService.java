package com.example.backend.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${spring.mail.host:}")
    private String mailHost;

    public void sendPasswordResetCode(String toEmail, String code) {
        if (mailHost == null || mailHost.isBlank()) {
            log.warn("SMTP no configurado. Codigo de recuperacion para {}: {}", toEmail, code);
            return;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.warn("JavaMailSender no disponible. Codigo de recuperacion para {}: {}", toEmail, code);
            return;
        }

        try {
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true, "UTF-8");
            if (fromEmail != null && !fromEmail.isBlank()) {
                helper.setFrom(fromEmail);
            }
            helper.setTo(toEmail);
            helper.setSubject("Tu codigo para recuperar la cuenta");
            helper.setText(buildPasswordResetText(code), buildPasswordResetHtml(code));

            mailSender.send(message);
        } catch (MailAuthenticationException ex) {
            log.error("Gmail rechazo las credenciales SMTP configuradas para {}", fromEmail);
            throw new RuntimeException("No se pudo autenticar el correo emisor. Revisa la contrasena de aplicacion de Gmail", ex);
        } catch (MailException ex) {
            log.error("No se pudo enviar el codigo de recuperacion a {}: {}", toEmail, ex.getMessage());
            throw new RuntimeException("No se pudo enviar el correo de recuperacion. Revisa la configuracion SMTP", ex);
        } catch (Exception ex) {
            log.error("No se pudo preparar el correo de recuperacion para {}: {}", toEmail, ex.getMessage());
            throw new RuntimeException("No se pudo enviar el correo de recuperacion", ex);
        }
    }

    private String buildPasswordResetText(String code) {
        return """
                Hola,

                Recibimos una solicitud para cambiar la contrasena de tu cuenta en El mundo de Mery.

                Tu codigo de verificacion es: %s

                Este codigo vence en 10 minutos. Si no solicitaste este cambio, ignora este correo.
                """.formatted(code);
    }

    private String buildPasswordResetHtml(String code) {
        return """
                <!doctype html>
                <html lang="es">
                  <head>
                    <meta charset="UTF-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                    <title>Recupera tu cuenta</title>
                  </head>
                  <body style="margin:0; padding:0; background:#f6efe8; font-family:Arial, Helvetica, sans-serif; color:#2d211b;">
                    <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#f6efe8; padding:32px 16px;">
                      <tr>
                        <td align="center">
                          <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="max-width:520px; background:#fffdfb; border:1px solid rgba(72,49,35,0.16); border-radius:24px; overflow:hidden; box-shadow:0 24px 60px rgba(50,29,18,0.16);">
                            <tr>
                              <td style="background:#2d211b; padding:26px 28px;">
                                <p style="margin:0; color:#d9b49d; font-size:12px; font-weight:700; letter-spacing:1.4px; text-transform:uppercase;">El mundo de Mery</p>
                                <h1 style="margin:8px 0 0; color:#ffffff; font-size:28px; line-height:1.15; font-weight:700;">Recupera tu cuenta</h1>
                              </td>
                            </tr>
                            <tr>
                              <td style="padding:28px;">
                                <p style="margin:0 0 14px; color:#2d211b; font-size:16px; line-height:1.6;">Hola,</p>
                                <p style="margin:0 0 22px; color:#70594b; font-size:15px; line-height:1.7;">
                                  Recibimos una solicitud para cambiar la contrasena de tu cuenta. Usa este codigo para continuar con la recuperacion:
                                </p>

                                <div style="margin:0 0 24px; padding:20px; border:1px solid rgba(165,111,83,0.28); border-radius:18px; background:#fffaf7; text-align:center;">
                                  <p style="margin:0 0 8px; color:#70594b; font-size:12px; font-weight:700; letter-spacing:1px; text-transform:uppercase;">Codigo de verificacion</p>
                                  <p style="margin:0; color:#2d211b; font-size:36px; line-height:1; font-weight:800; letter-spacing:8px;">%s</p>
                                </div>

                                <p style="margin:0 0 18px; color:#70594b; font-size:14px; line-height:1.7;">
                                  Este codigo vence en <strong style="color:#2d211b;">10 minutos</strong>. Si no solicitaste este cambio, puedes ignorar este correo.
                                </p>

                                <div style="border-top:1px solid rgba(78,54,39,0.12); padding-top:18px;">
                                  <p style="margin:0; color:#8b7667; font-size:12px; line-height:1.6;">
                                    Por seguridad, no compartas este codigo con nadie. El equipo de El mundo de Mery nunca te pedira tu codigo por otro medio.
                                  </p>
                                </div>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </body>
                </html>
                """.formatted(code);
    }
}
