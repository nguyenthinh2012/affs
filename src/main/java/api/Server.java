package api;

import io.vertx.core.DeploymentOptions;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class Server {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(1).setPreferNativeTransport(true));
        vertx.deployVerticle(AppApi.class.getName(), new DeploymentOptions().setInstances(1));
    }
}
