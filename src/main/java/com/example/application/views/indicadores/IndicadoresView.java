// src/main/java/com/example/application/views/indicadores/IndicadoresView.java
package com.example.application.views.indicadores;

import com.example.application.services.DataService;
import com.storedobject.chart.*;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.lineawesome.LineAwesomeIconUrl;
import reactor.core.Disposable;

@PageTitle("indicadores")
@Route("")
@Menu(order = 0, icon = LineAwesomeIconUrl.PENCIL_RULER_SOLID)
public class IndicadoresView extends Composite<VerticalLayout> {

    private final DataService service;
    private Disposable tempSubscription, humoSubscription;
    private int x = 0, x2 = 0;

    // Temp1 chart
    private final Data tempX = new Data(), tempY = new Data();
    private final SOChart tempChart = new SOChart();
    private final Span tempValueBox = new Span();

    // Humo1 chart
    private final Data humoX = new Data(), humoY = new Data();
    private final SOChart humoChart = new SOChart();
    private final Span humoValueBox = new Span();

    public IndicadoresView(DataService service) {
        this.service = service;
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");

        // Temp1 chart setup
        tempX.setName("Time");
        tempY.setName("Temp1");
        LineChart tempLine = new LineChart(tempX, tempY);
        tempLine.setName("Temp1");
        tempLine.plotOn(new RectangularCoordinate(new XAxis(tempX), new YAxis(tempY)));
        tempLine.setSmoothness(true);
        tempChart.setSize("400px", "300px");
        tempChart.add(tempLine, new Title("Temp1 Chart"));

        // Humo1 chart setup
        humoX.setName("Time");
        humoY.setName("Humo1");
        LineChart humoLine = new LineChart(humoX, humoY);
        humoLine.setName("Humo1");
        humoLine.plotOn(new RectangularCoordinate(new XAxis(humoX), new YAxis(humoY)));
        humoLine.setSmoothness(true);
        humoChart.setSize("400px", "300px");
        humoChart.add(humoLine, new Title("Humo1 Chart"));

        // Value boxes styling
        tempValueBox.getStyle().set("border", "1px solid #ccc").set("padding", "16px").set("border-radius", "8px").set("font-size", "1.5em");
        humoValueBox.getStyle().set("border", "1px solid #ccc").set("padding", "16px").set("border-radius", "8px").set("font-size", "1.5em");

        // Layouts
        HorizontalLayout tempLayout = new HorizontalLayout(tempChart, tempValueBox);
        tempLayout.setAlignItems(HorizontalLayout.Alignment.CENTER);
        tempLayout.setWidthFull();
        tempLayout.setSpacing(true);

        HorizontalLayout humoLayout = new HorizontalLayout(humoChart, humoValueBox);
        humoLayout.setAlignItems(HorizontalLayout.Alignment.CENTER);
        humoLayout.setWidthFull();
        humoLayout.setSpacing(true);

        getContent().add(tempLayout, humoLayout);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        UI ui = attachEvent.getUI();
        ui.setPollInterval(1000);

        // Temp1 subscription
        tempSubscription = service.getTemp1Stream().subscribe(temp -> {
            ui.access(() -> {
                x++;
                tempX.add(x);
                tempY.add(temp);
                tempValueBox.setText("Current Temp1: " + temp);
            });
        });

        // Humo1 subscription
        humoSubscription = service.getHumo1Stream().subscribe(humo -> {
            ui.access(() -> {
                x2++;
                humoX.add(x2);
                humoY.add(humo);
                humoValueBox.setText("Current Humo1: " + humo);
            });
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (tempSubscription != null) tempSubscription.dispose();
        if (humoSubscription != null) humoSubscription.dispose();
        super.onDetach(detachEvent);
    }
}