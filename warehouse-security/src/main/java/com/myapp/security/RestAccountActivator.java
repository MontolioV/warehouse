package com.myapp.security;

import com.myapp.communication.MailManager;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

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

    @EJB
    private AccountStore accountStore;
    @EJB
    private TokenStore tokenStore;
    @EJB
    private MailManager mailManager;
    @Context
    private UriInfo uriInfo;
    @Resource(lookup = "java:/strings/webAppAddress")
    private String webAppAddress;

    @Override
    @GET
    public Response activate(@QueryParam(QP_TOKEN) String tokenHash) throws URISyntaxException {
        accountStore.activateAccount(tokenHash);
        tokenStore.removeToken(tokenHash);
        return redirectHome();
    }

    @Override
    @GET
    @Path("/{login}/send-email")
    public Response prepareActivation(@PathParam("login")@NotBlank String login) throws MessagingException, URISyntaxException {
        Optional<Account> optionalAccount = accountStore.getAccountByLogin(login);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            Token token = tokenStore.createToken(account, TokenType.EMAIL_VERIFICATION, 1, ChronoUnit.DAYS);
            String uriActivation = uriInfo.getBaseUriBuilder().path(RestAccountActivator.class)
                    .queryParam(QP_TOKEN, token.getTokenHash()).build().toString();
            System.out.println("!@# " + uriActivation);
            String htmlText = String.format(MAIL_TEXT, account.getLogin(), uriActivation);
            mailManager.sendEmail(account.getEmail(), MAIL_SUBJECT, htmlText);
            return redirectHome();
        }
        return null;
    }

    private Response redirectHome() throws URISyntaxException {
        return Response.temporaryRedirect(new URI(webAppAddress)).build();
    }

    public String getWebAppAddress() {
        return webAppAddress;
    }

    public void setWebAppAddress(String webAppAddress) {
        this.webAppAddress = webAppAddress;
    }
}