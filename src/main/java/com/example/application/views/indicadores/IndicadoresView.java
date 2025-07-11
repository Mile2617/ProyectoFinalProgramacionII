package com.example.application.views.indicadores;

import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.example.application.services.DataService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;
import reactor.core.Disposable;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;

@PageTitle("Indicadores")
@Route("indicadores")
public class IndicadoresView extends Composite<VerticalLayout> {

    private final DataService service;
    private Disposable subscriptionTemp1;
    private Disposable subscriptionTemp2;
    private Disposable subscriptionHumo1;
    private Disposable subscriptionHumo2;
    private Disposable subscriptionFuego;

    // Chart data
    private final XYChart tempChart;
    private final Image tempChartImage;
    private final LinkedList<LocalDateTime> tempTimestamps = new LinkedList<>();
    private final LinkedList<Double> temp1Values = new LinkedList<>();
    private final LinkedList<Double> temp2Values = new LinkedList<>();

    private final XYChart humoChart;
    private final Image humoChartImage;
    private final LinkedList<LocalDateTime> humoTimestamps = new LinkedList<>();
    private final LinkedList<Integer> humo1Values = new LinkedList<>();
    private final LinkedList<Integer> humo2Values = new LinkedList<>();

    private final int MAX_POINTS = 20;

    // Value spans
    private final Span spanTempActual;
    private final Span spanHumoActual;
    private final Span spanIncendio;

    private double temp1 = 0.0;
    private double temp2 = 0.0;
    private int humo1 = 0;
    private int humo2 = 0;
    private boolean fuego = false;

    // Button state
    private boolean aspersoresEncendidos = false;
    private boolean alarmaEncendida = false;

    private Button btnEncenderAspersores;
    private Button btnApagarAspersores;
    private Button btnEncenderAlarma;
    private Button btnApagarAlarma;

    public IndicadoresView(DataService service) {
        this.service = service;

        // Chart setup
        tempChart = new XYChartBuilder()
                .width(400)
                .height(300)
                .title("Temperaturas")
                .xAxisTitle("Hora")
                .yAxisTitle("Temperatura (C)")
                .build();
        tempChart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        tempChart.getStyler().setDatePattern("HH:mm:ss");
        tempChart.getStyler().setXAxisLabelRotation(45);

        humoChart = new XYChartBuilder()
                .width(400)
                .height(300)
                .title("Humo")
                .xAxisTitle("Hora")
                .yAxisTitle("Nivel de Humo")
                .build();
        humoChart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        humoChart.getStyler().setDatePattern("HH:mm:ss");
        humoChart.getStyler().setXAxisLabelRotation(45);

        // Dummy data
        LocalDateTime now = LocalDateTime.now();
        tempTimestamps.add(now);
        temp1Values.add(0.0);
        temp2Values.add(0.0);

        humoTimestamps.add(now);
        humo1Values.add(0);
        humo2Values.add(0);

        tempChart.addSeries("Temp1", toDateList(tempTimestamps), new LinkedList<>(temp1Values)).setMarker(SeriesMarkers.CIRCLE);
        tempChart.addSeries("Temp2", toDateList(tempTimestamps), new LinkedList<>(temp2Values)).setMarker(SeriesMarkers.DIAMOND);

        humoChart.addSeries("Humo1", toDateList(humoTimestamps), new LinkedList<>(humo1Values)).setMarker(SeriesMarkers.CIRCLE);
        humoChart.addSeries("Humo2", toDateList(humoTimestamps), new LinkedList<>(humo2Values)).setMarker(SeriesMarkers.DIAMOND);

        tempChartImage = new Image();
        tempChartImage.setWidth("400px");
        tempChartImage.setHeight("300px");

        humoChartImage = new Image();
        humoChartImage.setWidth("400px");
        humoChartImage.setHeight("300px");

        spanTempActual = new Span();
        spanHumoActual = new Span();
        spanIncendio = new Span();

        updateInfoSpans();
        updateTempChartImage();
        updateHumoChartImage();

        // Layouts
        VerticalLayout tempPanel = new VerticalLayout(tempChartImage, spanTempActual);
        tempPanel.setAlignItems(FlexComponent.Alignment.CENTER);
        tempPanel.setPadding(false);

        VerticalLayout humoPanel = new VerticalLayout(humoChartImage, spanHumoActual);
        humoPanel.setAlignItems(FlexComponent.Alignment.CENTER);
        humoPanel.setPadding(false);

        HorizontalLayout chartsLayout = new HorizontalLayout(tempPanel, humoPanel);
        chartsLayout.setWidthFull();
        chartsLayout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.CENTER);

        // Sprinkler buttons
        btnEncenderAspersores = new Button("Encender aspersores", e -> encenderAspersores());
        btnApagarAspersores = new Button("Apagar aspersores", e -> apagarAspersores());
        btnApagarAspersores.setEnabled(false);

        HorizontalLayout buttonsAspersores = new HorizontalLayout(btnEncenderAspersores, btnApagarAspersores);
        buttonsAspersores.setJustifyContentMode(HorizontalLayout.JustifyContentMode.CENTER);
        buttonsAspersores.setWidthFull();

        // Alarm buttons
        btnEncenderAlarma = new Button("Encender alarma", e -> encenderAlarma());
        btnApagarAlarma = new Button("Apagar alarma", e -> apagarAlarma());
        btnApagarAlarma.setEnabled(false);

        HorizontalLayout buttonsAlarma = new HorizontalLayout(btnEncenderAlarma, btnApagarAlarma);
        buttonsAlarma.setJustifyContentMode(HorizontalLayout.JustifyContentMode.CENTER);
        buttonsAlarma.setWidthFull();

        // Main layout
        VerticalLayout mainLayout = getContent();
        mainLayout.setWidthFull();
        mainLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        mainLayout.add(chartsLayout, spanIncendio, buttonsAspersores, buttonsAlarma);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        UI ui = attachEvent.getUI();
        ui.setPollInterval(3000);

        subscriptionTemp1 = service.getTemp1Stream().subscribe(value -> ui.access(() -> {
            temp1 = value;
            addTempDataPoint();
        }));

        subscriptionTemp2 = service.getTemp2Stream().subscribe(value -> ui.access(() -> {
            temp2 = value;
            addTempDataPoint();
        }));

        subscriptionHumo1 = service.getHumo1Stream().subscribe(value -> ui.access(() -> {
            humo1 = value;
            addHumoDataPoint();
        }));

        subscriptionHumo2 = service.getHumo2Stream().subscribe(value -> ui.access(() -> {
            humo2 = value;
            addHumoDataPoint();
        }));

        subscriptionFuego = service.getFuegoStream().subscribe(value -> ui.access(() -> {
            fuego = value;
            updateInfoSpans();
        }));
    }

    private void addTempDataPoint() {
        LocalDateTime now = LocalDateTime.now();
        tempTimestamps.add(now);
        temp1Values.add(temp1);
        temp2Values.add(temp2);

        while (tempTimestamps.size() > MAX_POINTS) tempTimestamps.removeFirst();
        while (temp1Values.size() > MAX_POINTS) temp1Values.removeFirst();
        while (temp2Values.size() > MAX_POINTS) temp2Values.removeFirst();

        tempChart.updateXYSeries("Temp1", toDateList(tempTimestamps), new LinkedList<>(temp1Values), null);
        tempChart.updateXYSeries("Temp2", toDateList(tempTimestamps), new LinkedList<>(temp2Values), null);
        updateTempChartImage();
        updateInfoSpans();
    }

    private void addHumoDataPoint() {
        LocalDateTime now = LocalDateTime.now();
        humoTimestamps.add(now);
        humo1Values.add(humo1);
        humo2Values.add(humo2);

        while (humoTimestamps.size() > MAX_POINTS) humoTimestamps.removeFirst();
        while (humo1Values.size() > MAX_POINTS) humo1Values.removeFirst();
        while (humo2Values.size() > MAX_POINTS) humo2Values.removeFirst();

        humoChart.updateXYSeries("Humo1", toDateList(humoTimestamps), new LinkedList<>(humo1Values), null);
        humoChart.updateXYSeries("Humo2", toDateList(humoTimestamps), new LinkedList<>(humo2Values), null);
        updateHumoChartImage();
        updateInfoSpans();
    }

    private void updateTempChartImage() {
        try {
            byte[] imageBytes = BitmapEncoder.getBitmapBytes(tempChart, BitmapEncoder.BitmapFormat.PNG);
            tempChartImage.setSrc(new StreamResource("tempChart.png", () -> new ByteArrayInputStream(imageBytes)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateHumoChartImage() {
        try {
            byte[] imageBytes = BitmapEncoder.getBitmapBytes(humoChart, BitmapEncoder.BitmapFormat.PNG);
            humoChartImage.setSrc(new StreamResource("humoChart.png", () -> new ByteArrayInputStream(imageBytes)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateInfoSpans() {
        spanTempActual.setText(String.format("🌡 Temp1: %.2f°C | Temp2: %.2f°C", temp1, temp2));
        spanHumoActual.setText(String.format("💨 Humo1: %d | Humo2: %d", humo1, humo2));
        spanIncendio.setText(service.isRiesgo() ? "Riesgo" : "Sin riesgo");
    }

    private LinkedList<Date> toDateList(LinkedList<LocalDateTime> localDateTimes) {
        LinkedList<Date> dates = new LinkedList<>();
        for (LocalDateTime ldt : localDateTimes) {
            dates.add(Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant()));
        }
        return dates;
    }

    // Sprinkler logic
    private void encenderAspersores() {
        sendReleCommand(1);
        aspersoresEncendidos = true;
        btnEncenderAspersores.setEnabled(false);
        btnApagarAspersores.setEnabled(true);
        Notification.show("Aspersores encendidos");
    }

    private void apagarAspersores() {
        sendReleCommand(0);
        aspersoresEncendidos = false;
        btnEncenderAspersores.setEnabled(true);
        btnApagarAspersores.setEnabled(false);
        Notification.show("Aspersores apagados");
    }

    // Alarm logic
    private void encenderAlarma() {
        sendBuzzerCommand(1);
        alarmaEncendida = true;
        btnEncenderAlarma.setEnabled(false);
        btnApagarAlarma.setEnabled(true);
        Notification.show("Alarma encendida");
    }

    private void apagarAlarma() {
        sendBuzzerCommand(0);
        alarmaEncendida = false;
        btnEncenderAlarma.setEnabled(true);
        btnApagarAlarma.setEnabled(false);
        Notification.show("Alarma apagada");
    }

    // HTTP calls
    private void sendReleCommand(int value) {
        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("/api/dashboard/rele?value=" + value))
                    .POST(java.net.http.HttpRequest.BodyPublishers.noBody())
                    .build();
            client.sendAsync(request, java.net.http.HttpResponse.BodyHandlers.discarding());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendBuzzerCommand(int value) {
        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("/api/dashboard/buzzer?value=" + value))
                    .POST(java.net.http.HttpRequest.BodyPublishers.noBody())
                    .build();
            client.sendAsync(request, java.net.http.HttpResponse.BodyHandlers.discarding());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (subscriptionTemp1 != null) subscriptionTemp1.dispose();
        if (subscriptionTemp2 != null) subscriptionTemp2.dispose();
        if (subscriptionHumo1 != null) subscriptionHumo1.dispose();
        if (subscriptionHumo2 != null) subscriptionHumo2.dispose();
        if (subscriptionFuego != null) subscriptionFuego.dispose();
        super.onDetach(detachEvent);
    }
}