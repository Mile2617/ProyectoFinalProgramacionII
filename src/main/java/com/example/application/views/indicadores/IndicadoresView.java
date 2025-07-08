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
    private Disposable temp1Subscription, temp2Subscription, humo1Subscription, humo2Subscription, fuegoSubscription;
    private int x1 = 0, x2 = 0, x3 = 0, x4 = 0;

    // Temp1 chart
    private final Data temp1X = new Data(), temp1Y = new Data();
    private final SOChart temp1Chart = new SOChart();
    private final Span temp1ValueBox = new Span();

    // Temp2 chart
    private final Data temp2X = new Data(), temp2Y = new Data();
    private final SOChart temp2Chart = new SOChart();
    private final Span temp2ValueBox = new Span();

    // Humo1 chart
    private final Data humo1X = new Data(), humo1Y = new Data();
    private final SOChart humo1Chart = new SOChart();
    private final Span humo1ValueBox = new Span();

    // Humo2 chart
    private final Data humo2X = new Data(), humo2Y = new Data();
    private final SOChart humo2Chart = new SOChart();
    private final Span humo2ValueBox = new Span();

    // Fuego value box
    private final Span fuegoValueBox = new Span();

    public IndicadoresView(DataService service) {
        this.service = service;
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");

        // Temp1 chart setup
        temp1X.setName("Time");
        temp1Y.setName("Temp1");
        LineChart temp1Line = new LineChart(temp1X, temp1Y);
        temp1Line.setName("Temp1");
        temp1Line.plotOn(new RectangularCoordinate(new XAxis(temp1X), new YAxis(temp1Y)));
        temp1Line.setSmoothness(true);
        temp1Chart.setSize("400px", "300px");
        temp1Chart.add(temp1Line, new Title("Temp1 Chart"));

        // Temp2 chart setup
        temp2X.setName("Time");
        temp2Y.setName("Temp2");
        LineChart temp2Line = new LineChart(temp2X, temp2Y);
        temp2Line.setName("Temp2");
        temp2Line.plotOn(new RectangularCoordinate(new XAxis(temp2X), new YAxis(temp2Y)));
        temp2Line.setSmoothness(true);
        temp2Chart.setSize("400px", "300px");
        temp2Chart.add(temp2Line, new Title("Temp2 Chart"));

        // Humo1 chart setup
        humo1X.setName("Time");
        humo1Y.setName("Humo1");
        LineChart humo1Line = new LineChart(humo1X, humo1Y);
        humo1Line.setName("Humo1");
        humo1Line.plotOn(new RectangularCoordinate(new XAxis(humo1X), new YAxis(humo1Y)));
        humo1Line.setSmoothness(true);
        humo1Chart.setSize("400px", "300px");
        humo1Chart.add(humo1Line, new Title("Humo1 Chart"));

        // Humo2 chart setup
        humo2X.setName("Time");
        humo2Y.setName("Humo2");
        LineChart humo2Line = new LineChart(humo2X, humo2Y);
        humo2Line.setName("Humo2");
        humo2Line.plotOn(new RectangularCoordinate(new XAxis(humo2X), new YAxis(humo2Y)));
        humo2Line.setSmoothness(true);
        humo2Chart.setSize("400px", "300px");
        humo2Chart.add(humo2Line, new Title("Humo2 Chart"));

        // Value boxes styling
        temp1ValueBox.getStyle().set("border", "1px solid #ccc").set("padding", "16px").set("border-radius", "8px").set("font-size", "1.5em");
        temp2ValueBox.getStyle().set("border", "1px solid #ccc").set("padding", "16px").set("border-radius", "8px").set("font-size", "1.5em");
        humo1ValueBox.getStyle().set("border", "1px solid #ccc").set("padding", "16px").set("border-radius", "8px").set("font-size", "1.5em");
        humo2ValueBox.getStyle().set("border", "1px solid #ccc").set("padding", "16px").set("border-radius", "8px").set("font-size", "1.5em");
        fuegoValueBox.getStyle().set("border", "1px solid #ccc").set("padding", "16px").set("border-radius", "8px").set("font-size", "1.5em");

        // Layouts
        HorizontalLayout temp1Layout = new HorizontalLayout(temp1Chart, temp1ValueBox);
        temp1Layout.setAlignItems(HorizontalLayout.Alignment.CENTER);
        temp1Layout.setWidthFull();
        temp1Layout.setSpacing(true);

        HorizontalLayout temp2Layout = new HorizontalLayout(temp2Chart, temp2ValueBox);
        temp2Layout.setAlignItems(HorizontalLayout.Alignment.CENTER);
        temp2Layout.setWidthFull();
        temp2Layout.setSpacing(true);

        HorizontalLayout humo1Layout = new HorizontalLayout(humo1Chart, humo1ValueBox);
        humo1Layout.setAlignItems(HorizontalLayout.Alignment.CENTER);
        humo1Layout.setWidthFull();
        humo1Layout.setSpacing(true);

        HorizontalLayout humo2Layout = new HorizontalLayout(humo2Chart, humo2ValueBox);
        humo2Layout.setAlignItems(HorizontalLayout.Alignment.CENTER);
        humo2Layout.setWidthFull();
        humo2Layout.setSpacing(true);

        HorizontalLayout fuegoLayout = new HorizontalLayout(fuegoValueBox);
        fuegoLayout.setAlignItems(HorizontalLayout.Alignment.CENTER);
        fuegoLayout.setWidthFull();
        fuegoLayout.setSpacing(true);

        getContent().add(temp1Layout, temp2Layout, humo1Layout, humo2Layout, fuegoLayout);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        UI ui = attachEvent.getUI();
        ui.setPollInterval(1000);

        temp1Subscription = service.getTemp1Stream().subscribe(temp -> {
            ui.access(() -> {
                x1++;
                temp1X.add(x1);
                temp1Y.add(temp);
                temp1ValueBox.setText("Current Temp1: " + temp);
            });
        });

        temp2Subscription = service.getTemp2Stream().subscribe(temp -> {
            ui.access(() -> {
                x2++;
                temp2X.add(x2);
                temp2Y.add(temp);
                temp2ValueBox.setText("Current Temp2: " + temp);
            });
        });

        humo1Subscription = service.getHumo1Stream().subscribe(humo -> {
            ui.access(() -> {
                x3++;
                humo1X.add(x3);
                humo1Y.add(humo);
                humo1ValueBox.setText("Current Humo1: " + humo);
            });
        });

        humo2Subscription = service.getHumo2Stream().subscribe(humo -> {
            ui.access(() -> {
                x4++;
                humo2X.add(x4);
                humo2Y.add(humo);
                humo2ValueBox.setText("Current Humo2: " + humo);
            });
        });

        fuegoSubscription = service.getFuegoStream().subscribe(fuego -> {
            ui.access(() -> {
                fuegoValueBox.setText("Fuego: " + (fuego ? "Activo" : "No activo"));
            });
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (temp1Subscription != null) temp1Subscription.dispose();
        if (temp2Subscription != null) temp2Subscription.dispose();
        if (humo1Subscription != null) humo1Subscription.dispose();
        if (humo2Subscription != null) humo2Subscription.dispose();
        if (fuegoSubscription != null) fuegoSubscription.dispose();
        super.onDetach(detachEvent);
    }
}