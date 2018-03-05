package com.myapp.security.IT;

import com.myapp.security.Account;
import com.myapp.security.Roles;
import com.myapp.security.Token;
import com.myapp.security.TokenType;
import org.junit.Test;

import javax.json.bind.JsonbBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class AccountToken_JAXB_JSONB_IT {
    private JAXBContext jaxbContext = JAXBContext.newInstance(Account.class);
    private Marshaller marshaller = jaxbContext.createMarshaller();
    private Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();


    public AccountToken_JAXB_JSONB_IT() throws Exception {
    }

    @Test
    public void name() throws Exception {
        Token expectedToken = new Token(0, "sajdaj", TokenType.REMEMBER_ME, new Date());
        ArrayList<Roles> roles = new ArrayList<>();
        roles.add(Roles.ADMIN);
        Account expectedAccount = new Account(0, "test", "akdmsaldk", "test@test.com", new ArrayList<>(), roles);
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
