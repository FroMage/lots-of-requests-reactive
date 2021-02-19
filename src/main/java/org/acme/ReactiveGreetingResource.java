package org.acme;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.CompletionStageRxInvoker;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriInfo;

import io.smallrye.mutiny.Uni;

@Path("")
public class ReactiveGreetingResource {

    public static final int CALLS = 1000;
    public static final int CALL_DURATION_MS = 100;
    
    private AtomicInteger counter = new AtomicInteger();
    
    @Path("fan-out")
    @GET
    public Uni<String> fanOut(UriInfo uriInfo) {
        URI uri = uriInfo.getBaseUriBuilder().path(ReactiveGreetingResource.class, "remote").build();
        System.err.println("Starting fan-out to "+uri);
        WebTarget target = ClientBuilder.newClient().target(uri);
        CompletionStageRxInvoker invocation = target.request().rx();
        StringBuilder sb = new StringBuilder();
        Uni<String>[] calls = new Uni[CALLS];
        for(int i=0;i<CALLS;i++) {
            calls[i] = Uni.createFrom().completionStage(invocation.get(String.class));
        }
        System.err.println("Ending fan-out");
        return Uni.combine().all().unis(calls).combinedWith(vals -> {
            for(Object val : vals) {
                sb.append(val);
            }
            return sb.toString();
        });
    }
    
    @Path("remote")
    @GET
    public Uni<String> remote() throws InterruptedException {
        System.err.println("Remote call "+counter.incrementAndGet());
        return Uni.createFrom().item("OK").onItem().delayIt().by(Duration.ofMillis(CALL_DURATION_MS));
    }   
}