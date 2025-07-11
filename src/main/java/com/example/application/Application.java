package com.example.application;

    import com.example.application.threads.SensorRead;
    import com.vaadin.flow.component.page.AppShellConfigurator;
    import com.vaadin.flow.router.PageTitle;
    import com.vaadin.flow.theme.Theme;
    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;

    @SpringBootApplication
    @PageTitle("Indicadores2")
    @Theme(value = "indicadores")
    public class Application implements AppShellConfigurator {

        public static void main(String[] args) {
            SpringApplication.run(Application.class, args);
        }
    }
