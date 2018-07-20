package com.myapp.security.IT;

import com.myapp.security.Account;
import com.myapp.security.Roles;
import com.myapp.security.Token;
import org.junit.Ignore;
import org.junit.Test;

import javax.json.bind.JsonbBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import static com.myapp.security.TokenType.REMEMBER_ME;
import static com.myapp.utils.TestSecurityConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@Ignore
public class AccountToken_JAXB_JSONB_IT {
    private JAXBContext jaxbContext = JAXBContext.newInstance(Account.class);
    private Marshaller marshaller = jaxbContext.createMarshaller();
    private Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();


    public AccountToken_JAXB_JSONB_IT() throws Exception {
    }

    @Test
    public void name() throws Exception {
        Instant instant = Instant.now().plus(14, ChronoUnit.DAYS);
        Token expectedToken = new Token(0, TOKEN_HASH_VALID, REMEMBER_ME, new Date(), Date.from(instant));
        HashSet<Roles> roles = new HashSet<>();
        roles.add(Roles.ADMIN);
        Account expectedAccount = new Account(0, LOGIN_VALID, PASS_HASH_VALID, EMAIL_VALID, new ArrayList<>(), roles);
        expectedAccount.addToken(expectedToken);

        Account accountFromXML = null;
        Account accountFromJSON = null;
        Writer writer = new StringWriter();
        marshaller.marshal(expectedAccount, writer);
        accountFromXML = (Account) unmarshaller.unmarshal(new StringReader(writer.toString()));

        String jsonString = JsonbBuilder.newBuilder().build().toJson(expectedAccount);
        accountFromJSON = JsonbBuilder.newBuilder().build().fromJson(jsonString, Account.class);

        assertThat(accountFromXML, is(expectedAccount));
        assertThat(accountFromXML.getTokens().get(0), is(expectedToken));
        assertThat(accountFromJSON, is(expectedAccount));
        assertThat(accountFromJSON.getTokens().get(0), is(expectedToken));
    }
}
