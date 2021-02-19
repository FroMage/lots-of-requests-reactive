package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class ReactiveGreetingResourceTest {

    public static final String RESPONSE;
    static {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<ReactiveGreetingResource.CALLS;i++) {
            sb.append("OK");
        }
        RESPONSE = sb.toString();
    }

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/fan-out")
          .then()
             .statusCode(200)
             .body(is(RESPONSE));
    }

}