package com.example.proyectoJuegos.Views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("Login | GameHub")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        getStyle().set("background", "url('https://i.pinimg.com/736x/39/54/7b/39547b886e2c518abe91199ce75d1bd7.jpg')")
                .set("background-size", "cover")
                .set("background-position", "center");

        login.setAction("login");
        login.setForgotPasswordButtonVisible(false);

        login.getElement().getStyle()
                .set("background", "rgba(255, 255, 255, 0.1)")
                .set("backdrop-filter", "blur(12px)")
                .set("border", "1px solid rgba(255, 255, 255, 0.2)")
                .set("border-radius", "20px")
                .set("padding", "20px")
                .set("box-shadow", "0 8px 32px 0 rgba(0, 0, 0, 0.8)");

        H1 title = new H1("GAMEHUB");
        title.getStyle().set("color", "white").set("text-shadow", "2px 2px 4px #000");

        Button goToRegister = new Button("¿Eres nuevo? Crea una cuenta", e -> UI.getCurrent().navigate("register"));
        goToRegister.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);

        add(title, login, goToRegister);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            login.setError(true);
        }
    }

}
