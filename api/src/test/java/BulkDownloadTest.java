import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import revxrsal.zapper.download.DependencyDownloadResult;
import revxrsal.zapper.download.DownloadManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class BulkDownloadTest {


    List<String> urls = Arrays.asList(
            "https://repo1.maven.org/maven2/io/javalin/javalin/6.3.0/javalin-6.3.0.jar",
            "https://repo1.maven.org/maven2/io/javalin/community/routing/routing-annotated/6.1.6/routing-annotated-6.1.6.jar",
            "https://repo1.maven.org/maven2/io/javalin/community/routing/routing-dsl/6.1.6/routing-dsl-6.1.6.jar",
            "https://repo1.maven.org/maven2/io/javalin/community/routing/routing-core/6.1.6/routing-core-6.1.6.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-all/4.1.111.Final/netty-all-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/org/jetbrains/exposed/exposed-dao/0.51.0/exposed-dao-0.51.0.jar",
            "https://repo1.maven.org/maven2/org/jetbrains/exposed/exposed-jdbc/0.51.0/exposed-jdbc-0.51.0.jar",
            "https://repo1.maven.org/maven2/org/jetbrains/exposed/exposed-java-time/0.51.0/exposed-java-time-0.51.0.jar",
            "https://repo1.maven.org/maven2/org/jetbrains/exposed/exposed-core/0.51.0/exposed-core-0.51.0.jar",
            "https://repo1.maven.org/maven2/com/sksamuel/hoplite/hoplite-core/2.8.0.RC3/hoplite-core-2.8.0.RC3.jar",
            "https://repo1.maven.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-core-jvm/1.8.1/kotlinx-coroutines-core-jvm-1.8.1.jar",
            "https://repo1.maven.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-jdk8/1.8.1/kotlinx-coroutines-jdk8-1.8.1.jar",
            "https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-slf4j2-impl/2.23.1/log4j-slf4j2-impl-2.23.1.jar",
            "https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-core/2.23.1/log4j-core-2.23.1.jar",
            "https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-api/2.23.1/log4j-api-2.23.1.jar",
            "https://repo1.maven.org/maven2/io/github/revxrsal/lamp.cli/4.0.0-beta.19/lamp.cli-4.0.0-beta.19.jar",
            "https://repo1.maven.org/maven2/io/github/revxrsal/lamp.common/4.0.0-beta.19/lamp.common-4.0.0-beta.19.jar",
            "https://repo1.maven.org/maven2/com/github/docker-java/docker-java-core/3.4.0/docker-java-core-3.4.0.jar",
            "https://repo1.maven.org/maven2/com/github/docker-java/docker-java-transport-zerodep/3.4.0/docker-java-transport-zerodep-3.4.0.jar",
            "https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar",
            "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.18.1/jackson-databind-2.18.1.jar",
            "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.18.1/jackson-core-2.18.1.jar",
            "https://repo1.maven.org/maven2/com/github/docker-java/docker-java-api/3.4.0/docker-java-api-3.4.0.jar",
            "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.18.1/jackson-annotations-2.18.1.jar",
            "https://repo1.maven.org/maven2/com/fasterxml/jackson/dataformat/jackson-dataformat-yaml/2.18.1/jackson-dataformat-yaml-2.18.1.jar",
            "https://repo1.maven.org/maven2/org/eclipse/jetty/websocket/websocket-jetty-server/11.0.23/websocket-jetty-server-11.0.23.jar",
            "https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-webapp/11.0.23/jetty-webapp-11.0.23.jar",
            "https://repo1.maven.org/maven2/org/eclipse/jetty/websocket/websocket-servlet/11.0.23/websocket-servlet-11.0.23.jar",
            "https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-servlet/11.0.23/jetty-servlet-11.0.23.jar",
            "https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-security/11.0.23/jetty-security-11.0.23.jar",
            "https://repo1.maven.org/maven2/org/eclipse/jetty/websocket/websocket-core-server/11.0.23/websocket-core-server-11.0.23.jar",
            "https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-server/11.0.23/jetty-server-11.0.23.jar",
            "https://repo1.maven.org/maven2/org/eclipse/jetty/websocket/websocket-jetty-common/11.0.23/websocket-jetty-common-11.0.23.jar",
            "https://repo1.maven.org/maven2/org/eclipse/jetty/websocket/websocket-core-common/11.0.23/websocket-core-common-11.0.23.jar",
            "https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-http/11.0.23/jetty-http-11.0.23.jar",
            "https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-io/11.0.23/jetty-io-11.0.23.jar",
            "https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-xml/11.0.23/jetty-xml-11.0.23.jar",
            "https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-util/11.0.23/jetty-util-11.0.23.jar",
            "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.16/slf4j-api-2.0.16.jar",
            "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-reflect/1.9.22/kotlin-reflect-1.9.22.jar",
            "https://repo1.maven.org/maven2/io/javalin/community/routing/routing-annotated-specification/6.1.6/routing-annotated-specification-6.1.6.jar",
            "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-jdk7/1.9.25/kotlin-stdlib-jdk7-1.9.25.jar",
            "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/1.9.25/kotlin-stdlib-1.9.25.jar",
            "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-jdk8/1.9.25/kotlin-stdlib-jdk8-1.9.25.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-transport-native-epoll/4.1.111.Final/netty-transport-native-epoll-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-transport-native-epoll/4.1.111.Final/netty-transport-native-epoll-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-transport-native-epoll/4.1.111.Final/netty-transport-native-epoll-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-transport-native-kqueue/4.1.111.Final/netty-transport-native-kqueue-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-transport-native-kqueue/4.1.111.Final/netty-transport-native-kqueue-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-transport-classes-epoll/4.1.111.Final/netty-transport-classes-epoll-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-transport-classes-kqueue/4.1.111.Final/netty-transport-classes-kqueue-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-resolver-dns-native-macos/4.1.111.Final/netty-resolver-dns-native-macos-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-resolver-dns-native-macos/4.1.111.Final/netty-resolver-dns-native-macos-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-resolver-dns-classes-macos/4.1.111.Final/netty-resolver-dns-classes-macos-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-resolver-dns/4.1.111.Final/netty-resolver-dns-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-handler/4.1.111.Final/netty-handler-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-transport-native-unix-common/4.1.111.Final/netty-transport-native-unix-common-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-codec-dns/4.1.111.Final/netty-codec-dns-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-codec/4.1.111.Final/netty-codec-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-transport/4.1.111.Final/netty-transport-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-buffer/4.1.111.Final/netty-buffer-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-codec-haproxy/4.1.111.Final/netty-codec-haproxy-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-codec-http/4.1.111.Final/netty-codec-http-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-codec-http2/4.1.111.Final/netty-codec-http2-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-codec-memcache/4.1.111.Final/netty-codec-memcache-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-codec-mqtt/4.1.111.Final/netty-codec-mqtt-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-codec-redis/4.1.111.Final/netty-codec-redis-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-codec-smtp/4.1.111.Final/netty-codec-smtp-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-codec-socks/4.1.111.Final/netty-codec-socks-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-codec-stomp/4.1.111.Final/netty-codec-stomp-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-codec-xml/4.1.111.Final/netty-codec-xml-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-resolver/4.1.111.Final/netty-resolver-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-common/4.1.111.Final/netty-common-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-handler-proxy/4.1.111.Final/netty-handler-proxy-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-handler-ssl-ocsp/4.1.111.Final/netty-handler-ssl-ocsp-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-transport-rxtx/4.1.111.Final/netty-transport-rxtx-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-transport-sctp/4.1.111.Final/netty-transport-sctp-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/io/netty/netty-transport-udt/4.1.111.Final/netty-transport-udt-4.1.111.Final.jar",
            "https://repo1.maven.org/maven2/com/github/docker-java/docker-java-transport/3.4.0/docker-java-transport-3.4.0.jar",
            "https://repo1.maven.org/maven2/commons-io/commons-io/2.13.0/commons-io-2.13.0.jar",
            "https://repo1.maven.org/maven2/org/apache/commons/commons-compress/1.21/commons-compress-1.21.jar",
            "https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.12.0/commons-lang3-3.12.0.jar",
            "https://repo1.maven.org/maven2/com/google/guava/guava/19.0/guava-19.0.jar",
            "https://repo1.maven.org/maven2/org/bouncycastle/bcpkix-jdk18on/1.76/bcpkix-jdk18on-1.76.jar",
            "https://repo1.maven.org/maven2/net/java/dev/jna/jna/5.13.0/jna-5.13.0.jar",
            "https://repo1.maven.org/maven2/org/yaml/snakeyaml/2.3/snakeyaml-2.3.jar",
            "https://repo1.maven.org/maven2/org/eclipse/jetty/toolchain/jetty-jakarta-servlet-api/5.0.2/jetty-jakarta-servlet-api-5.0.2.jar",
            "https://repo1.maven.org/maven2/org/eclipse/jetty/websocket/websocket-jetty-api/11.0.23/websocket-jetty-api-11.0.23.jar",
            "https://repo1.maven.org/maven2/org/bouncycastle/bcutil-jdk18on/1.76/bcutil-jdk18on-1.76.jar",
            "https://repo1.maven.org/maven2/org/bouncycastle/bcprov-jdk18on/1.76/bcprov-jdk18on-1.76.jar",
            "https://repo1.maven.org/maven2/org/jetbrains/annotations/23.0.0/annotations-23.0.0.jar");


    @Test
    public void testSingleThreadDownload() throws IOException, InterruptedException {

        List<Long> timings = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            List<DownloadManager.DownloadTask> tasks = new ArrayList<>();
            ExecutorService executorService = Executors.newSingleThreadExecutor();

            Instant start = Instant.now();

            for (String s : urls) {

                URL url = new URL(s);

                URLConnection connection = url.openConnection();

                tasks.add(new DownloadManager.DownloadTask(connection, Files.newOutputStream(File.createTempFile("123", UUID.randomUUID().toString().substring(0, 8)).toPath())));
            }

            List<CompletableFuture<DependencyDownloadResult>> results = new ArrayList<>();

            for (DownloadManager.DownloadTask task : tasks) {
                results.add(CompletableFuture.supplyAsync(task, executorService));
            }

            CompletableFuture.allOf(results.toArray(new CompletableFuture[0])).join();


            timings.add(Instant.now().toEpochMilli() - start.toEpochMilli());

            Thread.sleep(1000);
        }

        System.out.println("Single thread download timings: " + timings);
        System.out.println("Average: " + timings.stream().skip(1).mapToLong(Long::longValue).average().orElse(0));

    }

    @Test
    public void test20ThreadExecutor() throws IOException, InterruptedException {

        List<Long> timings = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            List<DownloadManager.DownloadTask> tasks = new ArrayList<>();
            ExecutorService executorService = Executors.newFixedThreadPool(20);

            Instant start = Instant.now();

            for (String s : urls) {

                URL url = new URL(s);

                URLConnection connection = url.openConnection();

                tasks.add(new DownloadManager.DownloadTask(connection, Files.newOutputStream(File.createTempFile("123", UUID.randomUUID().toString().substring(0, 8)).toPath())));
            }

            List<CompletableFuture<DependencyDownloadResult>> results = new ArrayList<>();

            for (DownloadManager.DownloadTask task : tasks) {
                results.add(CompletableFuture.supplyAsync(task, executorService));
            }

            CompletableFuture.allOf(results.toArray(new CompletableFuture[0])).join();


            timings.add(Instant.now().toEpochMilli() - start.toEpochMilli());

            Thread.sleep(1000);
        }

        System.out.println("Single thread download timings: " + timings);
        System.out.println("Average: " + timings.stream().skip(1).mapToLong(Long::longValue).average().orElse(0));

    }

}