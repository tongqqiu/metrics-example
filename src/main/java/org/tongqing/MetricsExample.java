package org.tongqing;

import com.codahale.metrics.*;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;

public class MetricsExample {
    static final MetricRegistry metrics = new MetricRegistry();
    private static final Timer testTimer = metrics.timer(name(MetricsExample.class, "timer"));

    public static void main(String args[]) {
        startReport();
        final Timer.Context context = testTimer.time();

        for(int i=0; i<100; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                context.stop();
            }

        }
    }

    static void startReport() {

        ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(1, TimeUnit.SECONDS);


        final Graphite graphite = new Graphite(new InetSocketAddress("10.10.16.38", 2003));
        final GraphiteReporter graphiteReporter = GraphiteReporter.forRegistry(metrics)
                .prefixedWith("tqiu")
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build(graphite);
        graphiteReporter.start(1, TimeUnit.SECONDS);
    }
}