package com.example.application.views.indicadores;

import com.example.application.services.DataService;
import com.storedobject.chart.*;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import org.vaadin.lineawesome.LineAwesomeIconUrl;
import reactor.core.Disposable;

import java.util.Random;

@PageTitle("indicadores")
@Route("")
@Menu(order = 0, icon = LineAwesomeIconUrl.PENCIL_RULER_SOLID)
public class IndicadoresView extends Composite<VerticalLayout> {

    private DataService service;
    private Disposable subscription;
    LineChart lineChart;
    Data xValues = new Data(), yValues = new Data();
    int x=0;
    SOChart soChart;
    private final DataChannel dataChannel;

    public IndicadoresView(DataService service) {
        this.service= service;
        HorizontalLayout layoutRow = new HorizontalLayout();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        layoutRow.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow);
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");


        soChart = new SOChart();
        soChart.setSize("800px", "500px");

        // Generating some random values for a LineChart
        Random random = new Random();



        xValues.setName("X Values");
        yValues.setName("Random Values");

        // Line chart is initialized with the generated XY values
        lineChart = new LineChart(xValues, yValues);
        lineChart.setName("40 Random Values");
        System.out.println("prueba 1234");
        // Line chart needs a coordinate system to plot on
        // We need Number-type for both X and Y axes in this case
        XAxis xAxis = new XAxis(xValues);
        xAxis.setMinAsMinData();
        xAxis.setMaxAsMaxData();
        YAxis yAxis = new YAxis(yValues);
        yAxis.setMin(0);
        yAxis.setMax(100);

        //XAxis xAxis = new XAxis(DataType.NUMBER);
        //YAxis yAxis = new YAxis(DataType.NUMBER);
        RectangularCoordinate rc = new RectangularCoordinate(xAxis, yAxis);
        lineChart.plotOn(rc);
        lineChart.setSmoothness(true);

        // Add to the chart display area with a simple title
        soChart.add(lineChart, new Title("Sample Line Chart"));
        // Set the component for the view


        dataChannel = new DataChannel(soChart, xValues, yValues);
        layoutRow.add(soChart);






        getContent().add(layoutRow);
    }


    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        UI ui = attachEvent.getUI();
        ui.setPollInterval(1000);

        // Hook up to service for live updates
        subscription =
                service
                        .getStockPrice()
                        .subscribe(
                                price -> {
                                    ui.access(
                                            () -> {


                                                try {
                                                    x++;
                                                    dataChannel.append(x,price);

                                                    //System.out.println(x+" : "+price);
                                                } catch (Exception e) {
                                                    throw new RuntimeException(e);
                                                }

                                            }
                                    );
                                }
                        );
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // Cancel subscription when the view is detached
        subscription.dispose();

        super.onDetach(detachEvent);
    }

}
