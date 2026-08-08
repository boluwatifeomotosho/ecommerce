<#assign inviterName = (user.attributes.invitedBy)!"" />
<#assign inviterCompany = (user.attributes.invitedByCompany)!"" />
Hi ${user.firstName!"there"},

<#if inviterName?has_content && inviterCompany?has_content>
${inviterName} has invited you to join ${inviterCompany} on Pinepetosan Marketplace — Nigeria's most trusted marketplace for verified vendors and fast nationwide delivery.
<#elseif inviterCompany?has_content>
You've been invited to join ${inviterCompany} on Pinepetosan Marketplace — Nigeria's most trusted marketplace for verified vendors and fast nationwide delivery.
<#else>
You've been invited to join Pinepetosan Marketplace — Nigeria's most trusted marketplace for verified vendors and fast nationwide delivery.
</#if>

To activate your account, set your password by opening the link below in your browser:

${link}

Once your password is set, you'll be able to log in with:
  • Email:    ${user.email}
  • Password: the one you just created

This invitation link expires in ${linkExpirationFormatter(linkExpiration)}.

Didn't expect this? You can safely ignore this email — no account will be activated without your password.

Welcome aboard,
The Pinepetosan Marketplace Team

---
© Pinepetosan Marketplace · Secure authentication
This is an automated message. Please do not reply directly to this email.
