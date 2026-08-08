<#outputformat "plainText">
<#assign requiredActionsText><#if requiredActions??><#list requiredActions><#items as reqActionItem>${msg("requiredAction.${reqActionItem}")}<#sep>, </#items></#list><#else>${msg("executeActions")}</#if></#assign>
</#outputformat>
<#assign inviterName = (user.attributes.invitedBy)!"" />
<#assign inviterCompany = (user.attributes.invitedByCompany)!"" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <meta name="color-scheme" content="light"/>
    <meta name="supported-color-schemes" content="light"/>
    <title>You're invited to Pinepetosan Marketplace</title>
    <style type="text/css">
        /* Client resets */
        body, table, td, a { -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }
        table, td { mso-table-lspace: 0pt; mso-table-rspace: 0pt; }
        img { -ms-interpolation-mode: bicubic; border: 0; outline: none; text-decoration: none; }
        body { margin: 0 !important; padding: 0 !important; width: 100% !important; }

        a { color: #7c3aed; text-decoration: none; }
        a:hover { text-decoration: underline; }

        /* Mobile */
        @media only screen and (max-width: 600px) {
            .pmk-container { width: 100% !important; max-width: 100% !important; }
            .pmk-px { padding-left: 24px !important; padding-right: 24px !important; }
            .pmk-hero-title { font-size: 24px !important; line-height: 32px !important; }
            .pmk-btn { display: block !important; width: 100% !important; box-sizing: border-box; }
        }
    </style>
</head>
<body style="margin: 0; padding: 0; background-color: #f5f3ff; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; color: #1f2937;">

<span style="display: none !important; visibility: hidden; opacity: 0; color: transparent; height: 0; width: 0; overflow: hidden;">
    Welcome to Pinepetosan Marketplace. Set your password to activate your account.
</span>

<table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color: #f5f3ff;">
    <tr>
        <td align="center" style="padding: 32px 16px;">
            <table role="presentation" class="pmk-container" width="600" cellpadding="0" cellspacing="0" border="0" style="width: 100%; max-width: 600px; background-color: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 20px rgba(30, 41, 59, 0.06);">

                <!-- Header -->
                <tr>
                    <td align="center" style="background: linear-gradient(135deg, #7c3aed 0%, #6366f1 100%); background-color: #7c3aed; padding: 40px 24px 32px;">
                        <table role="presentation" cellpadding="0" cellspacing="0" border="0">
                            <tr>
                                <td align="center" style="color: #ffffff; font-size: 22px; font-weight: 700; letter-spacing: -0.01em;">
                                    <span style="display: inline-block; background: rgba(255,255,255,0.15); width: 44px; height: 44px; line-height: 44px; text-align: center; border-radius: 12px; margin-right: 10px; vertical-align: middle;">
                                        <span style="font-size: 22px;">&#128722;</span>
                                    </span>
                                    <span style="vertical-align: middle;">Pinepetosan Marketplace</span>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>

                <!-- Hero -->
                <tr>
                    <td class="pmk-px" style="padding: 40px 40px 8px;">
                        <h1 class="pmk-hero-title" style="margin: 0 0 12px; font-size: 28px; line-height: 36px; font-weight: 700; color: #111827; letter-spacing: -0.02em;">
                            You're invited to join Pinepetosan Marketplace
                        </h1>
                        <p style="margin: 0 0 24px; font-size: 16px; line-height: 24px; color: #4b5563;">
                            Hi ${user.firstName!"there"},
                        </p>
                        <p style="margin: 0 0 24px; font-size: 16px; line-height: 24px; color: #4b5563;">
                            <#if inviterName?has_content && inviterCompany?has_content>
                                <strong style="color: #111827;">${inviterName}</strong> has invited you to join <strong style="color: #111827;">${inviterCompany}</strong> on Pinepetosan Marketplace &mdash; Nigeria's most trusted marketplace for verified vendors and fast nationwide delivery.
                            <#elseif inviterCompany?has_content>
                                You've been invited to join <strong style="color: #111827;">${inviterCompany}</strong> on Pinepetosan Marketplace &mdash; Nigeria's most trusted marketplace for verified vendors and fast nationwide delivery.
                            <#else>
                                Someone on your team has invited you to Pinepetosan Marketplace &mdash; Nigeria's most trusted marketplace for verified vendors and fast nationwide delivery.
                            </#if>
                        </p>
                        <p style="margin: 0 0 32px; font-size: 16px; line-height: 24px; color: #4b5563;">
                            To activate your account, just create a password below. It only takes a minute.
                        </p>
                    </td>
                </tr>

                <!-- Button -->
                <tr>
                    <td class="pmk-px" align="center" style="padding: 0 40px 8px;">
                        <table role="presentation" cellpadding="0" cellspacing="0" border="0" style="margin: 0 auto;">
                            <tr>
                                <td align="center" bgcolor="#7c3aed"
                                    style="border-radius: 12px; background: linear-gradient(135deg, #7c3aed 0%, #a855f7 100%); background-color: #7c3aed;">
                                    <a class="pmk-btn" href="${link}"
                                       style="display: inline-block; padding: 16px 36px; font-size: 16px; font-weight: 600; color: #ffffff !important; text-decoration: none; border-radius: 12px; letter-spacing: -0.01em; line-height: 1;">
                                        Set your password &rarr;
                                    </a>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>

                <!-- Fallback link -->
                <tr>
                    <td class="pmk-px" style="padding: 24px 40px 8px;">
                        <p style="margin: 0 0 8px; font-size: 13px; line-height: 20px; color: #9ca3af;">
                            Button not working? Copy and paste this link into your browser:
                        </p>
                        <p style="margin: 0; font-size: 13px; line-height: 20px; word-break: break-all;">
                            <a href="${link}" style="color: #7c3aed; text-decoration: underline;">${link}</a>
                        </p>
                    </td>
                </tr>

                <!-- Info box -->
                <tr>
                    <td class="pmk-px" style="padding: 24px 40px 8px;">
                        <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0"
                               style="background-color: #f5f3ff; border: 1px solid #ede9fe; border-radius: 12px;">
                            <tr>
                                <td style="padding: 16px 20px;">
                                    <p style="margin: 0 0 8px; font-size: 13px; font-weight: 600; color: #7c3aed; text-transform: uppercase; letter-spacing: 0.05em;">
                                        What happens next
                                    </p>
                                    <p style="margin: 0 0 6px; font-size: 14px; line-height: 22px; color: #4b5563;">
                                        &bull; You'll set a password of your choice.
                                    </p>
                                    <p style="margin: 0 0 6px; font-size: 14px; line-height: 22px; color: #4b5563;">
                                        &bull; Log in with <strong style="color: #111827;">${user.email}</strong> plus the password you just created.
                                    </p>
                                    <p style="margin: 0; font-size: 14px; line-height: 22px; color: #4b5563;">
                                        &bull; This invitation link expires in <strong style="color: #111827;">${linkExpirationFormatter(linkExpiration)}</strong>.
                                    </p>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>

                <!-- Sign off -->
                <tr>
                    <td class="pmk-px" style="padding: 32px 40px 0;">
                        <p style="margin: 0 0 4px; font-size: 15px; line-height: 22px; color: #4b5563;">
                            Welcome aboard,
                        </p>
                        <p style="margin: 0; font-size: 15px; line-height: 22px; color: #111827; font-weight: 600;">
                            The Pinepetosan Marketplace Team
                        </p>
                    </td>
                </tr>

                <!-- Divider -->
                <tr>
                    <td class="pmk-px" style="padding: 32px 40px 0;">
                        <hr style="border: none; border-top: 1px solid #ede9fe; margin: 0;"/>
                    </td>
                </tr>

                <!-- Safety note + Footer -->
                <tr>
                    <td class="pmk-px" style="padding: 20px 40px 32px;">
                        <p style="margin: 0 0 8px; font-size: 12px; line-height: 18px; color: #9ca3af;">
                            Didn't expect this? You can safely ignore this email &mdash; no account will be activated without your password.
                        </p>
                        <p style="margin: 0; font-size: 12px; line-height: 18px; color: #9ca3af;">
                            &copy; Pinepetosan Marketplace &middot; Secure authentication powered by Keycloak
                        </p>
                    </td>
                </tr>
            </table>

            <table role="presentation" class="pmk-container" width="600" cellpadding="0" cellspacing="0" border="0" style="width: 100%; max-width: 600px;">
                <tr>
                    <td align="center" style="padding: 16px 24px 0; font-size: 11px; line-height: 16px; color: #a78bfa;">
                        This is an automated message. Please do not reply directly to this email.
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>

</body>
</html>
