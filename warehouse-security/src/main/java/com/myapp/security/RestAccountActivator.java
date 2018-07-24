package com.myapp.security;

import com.myapp.communication.MailManager;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.time.temporal.ChronoUnit;

/**
 * <p>Created by MontolioV on 17.07.18.
 */
@Stateless
@Path("/activation")
public class RestAccountActivator implements AccountActivator {
    public static final String MAIL_SUBJECT = "Account verification";
    public static final String MAIL_TEXT = "<h1>Hi, %s!</h1>" +
            "<p>Follow <a href='%s'>link</a> to verify your account:</p>";
    public static final String QP_TOKEN = "token";

    // TODO: 24.07.18 NPE AccountStore
    @EJB
    private AccountStore accountStore;
    @EJB
    private TokenStore tokenStore;
    @EJB
    private MailManager mailManager;
    @Context
    private UriInfo uriInfo;

    @GET
    public void activate(@QueryParam(QP_TOKEN) String tokenHash) {
        accountStore.getAccountByTokenHash(tokenHash).ifPresent(account -> {
            accountStore.changeAccountStatus(account.getId(), true);
            tokenStore.removeToken(tokenHash);
        });
    }

    // TODO: 24.07.18 Make asynchronous
    @Override
    public void prepareActivation(@NotNull Account account) throws MessagingException {
        Token token = tokenStore.createToken(account, TokenType.EMAIL_VERIFICATION, 1, ChronoUnit.DAYS);
        // TODO: 24.07.18 Path builder doesn't work
//        String uriActivation = uriInfo.getAbsolutePathBuilder().queryParam(QP_TOKEN, token.getTokenHash()).build().toString();
        String uriActivation = "http://37.229.148.120/warehouse/rs/activation?" + QP_TOKEN + "=" + token.getTokenHash();
        String htmlText = String.format(MAIL_TEXT, account.getLogin(), uriActivation);
        mailManager.sendEmail(account.getEmail(), MAIL_SUBJECT, htmlText);
    }
}
