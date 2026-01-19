package com.example.proyectoJuegos.Views;

import com.example.proyectoJuegos.Entities.Juego;
import com.example.proyectoJuegos.Entities.Review;
import com.example.proyectoJuegos.Services.JuegoService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.util.List;

@PageTitle("Catálogo de Juegos | Admin")
@Route("")
@PermitAll
public class MainView extends VerticalLayout {

    private final Grid<Juego> grid = new Grid<>(Juego.class, false);

    // Formulario
    private final TextField titulo = new TextField("Título del Videojuego");
    private final TextField urlImagen = new TextField("URL de la Portada");
    private final DatePicker fechaSalida = new DatePicker("Fecha de Lanzamiento");
    private final TextArea descripcion = new TextArea("Descripción");

    // Componente de imagen grande
    private final Image portadaDetalle = new Image("https://via.placeholder.com/400x200?text=Sin+Imagen", "Portada");
    private final VerticalLayout reviewsLayout = new VerticalLayout();

    private final Button cancel = new Button("Cancelar", new Icon(VaadinIcon.CLOSE));
    private final Button save = new Button("Guardar Juego", new Icon(VaadinIcon.CHECK));
    private final Button delete = new Button("Eliminar", new Icon(VaadinIcon.TRASH));

    private final Binder<Juego> binder = new Binder<>(Juego.class);
    private final JuegoService juegoService;
    private Juego juegoSeleccionado;

    public MainView(JuegoService juegoService) {
        this.juegoService = juegoService;
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        // Header
        H1 headerTitle = new H1("GameHub Manager");
        headerTitle.getStyle().set("font-size", "1.5rem").set("margin", "0 1rem");
        HorizontalLayout header = new HorizontalLayout(new Icon(VaadinIcon.GAMEPAD), headerTitle);
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.getStyle().set("background-color", "var(--lumo-base-color)")
                .set("border-bottom", "1px solid var(--lumo-contrast-10pct)")
                .set("padding", "1rem");

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();
        splitLayout.addToPrimary(createGridLayout());
        splitLayout.addToSecondary(createEditorLayout());

        add(header, splitLayout);

        binder.bindInstanceFields(this);
        grid.asSingleSelect().addValueChangeListener(event -> populateForm(event.getValue()));

        cancel.addClickListener(e -> clearForm());
        save.addClickListener(e -> saveJuego());
        delete.addClickListener(e -> deleteJuego());

        refreshGrid();
        clearForm();
    }

    private Component createGridLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        TextField filterText = new TextField();
        filterText.setPlaceholder("Buscar juego...");
        filterText.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        filterText.setClearButtonVisible(true);

        Button addGameBtn = new Button("Nuevo Juego", new Icon(VaadinIcon.PLUS));
        addGameBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addGameBtn.addClickListener(e -> {
            grid.asSingleSelect().clear();
            populateForm(new Juego());
        });

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addGameBtn);
        toolbar.setWidthFull();
        toolbar.expand(filterText);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);

        // --- COLUMNA DE IMAGEN (MINIATURA) ---
        grid.addComponentColumn(j -> {
            String url = (j.getUrlImagen() == null || j.getUrlImagen().isEmpty())
                    ? "https://via.placeholder.com/50" : j.getUrlImagen();
            Image img = new Image(url, "Portada");
            img.getStyle()
                    .set("width", "45px")
                    .set("height", "45px")
                    .set("object-fit", "cover")
                    .set("border-radius", "50%")
                    .set("border", "2px solid #ddd");
            return img;
        }).setHeader("Vista").setWidth("70px").setFlexGrow(0);

        grid.addColumn(Juego::getTitulo).setHeader("Título").setSortable(true).setFlexGrow(1);
        grid.addColumn(Juego::getFechaSalida).setHeader("Fecha").setAutoWidth(true);

        grid.addComponentColumn(j -> {
            Span badge = new Span(String.valueOf(j.getReviews() != null ? j.getReviews().size() : 0));
            badge.getElement().getThemeList().add("badge pill");
            return badge;
        }).setHeader("Reseñas").setAutoWidth(true);

        layout.add(toolbar, grid);
        return layout;
    }

    private Component createEditorLayout() {
        VerticalLayout editorLayout = new VerticalLayout();
        editorLayout.setPadding(true);
        editorLayout.getStyle().set("background-color", "#fcfcfc").set("overflow-y", "auto");

        // --- SECCIÓN IMAGEN DETALLE (GRANDE) ---
        Div imageContainer = new Div(portadaDetalle);
        imageContainer.setWidthFull();
        imageContainer.getStyle()
                .set("height", "220px")
                .set("overflow", "hidden")
                .set("border-radius", "12px")
                .set("background", "#000")
                .set("margin-bottom", "1rem");

        portadaDetalle.setWidthFull();
        portadaDetalle.getStyle().set("object-fit", "cover").set("height", "100%");

        H3 sectionTitle = new H3("Gestión de Información");
        sectionTitle.getStyle().set("margin", "0");

        FormLayout formLayout = new FormLayout();
        urlImagen.setPlaceholder("http://ejemplo.com/imagen.jpg");
        formLayout.add(titulo, fechaSalida, urlImagen, descripcion);
        formLayout.setColspan(descripcion, 2);
        formLayout.setColspan(urlImagen, 2);

        HorizontalLayout buttonLayout = new HorizontalLayout(save, delete, cancel);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        buttonLayout.getStyle().set("margin-top", "1rem");

        editorLayout.add(imageContainer, sectionTitle, formLayout, buttonLayout, new Hr(), new H4("Feedback"), reviewsLayout);
        return editorLayout;
    }

    private void populateForm(Juego value) {
        this.juegoSeleccionado = value;
        binder.readBean(this.juegoSeleccionado);

        if (value != null) {
            // Actualizar imagen grande
            String url = (value.getUrlImagen() == null || value.getUrlImagen().isEmpty())
                    ? "https://via.placeholder.com/400x200?text=Sin+Imagen" : value.getUrlImagen();
            portadaDetalle.setSrc(url);

            cargarResenasVisuales(value.getReviews());
            delete.setEnabled(value.getId() != null);
        } else {
            portadaDetalle.setSrc("https://via.placeholder.com/400x200?text=Selecciona+un+Juego");
            reviewsLayout.removeAll();
            delete.setEnabled(false);
        }
    }

    // ... (Mantén tus métodos cargarResenasVisuales, refreshGrid, clearForm, saveJuego y deleteJuego igual)

    private void cargarResenasVisuales(List<Review> reviews) {
        reviewsLayout.removeAll();
        if (reviews == null || reviews.isEmpty()) {
            Span empty = new Span("Aún no hay críticas.");
            empty.getStyle().set("color", "gray").set("font-style", "italic");
            reviewsLayout.add(empty);
        } else {
            for (Review r : reviews) {
                Div card = new Div();
                card.setWidthFull();
                card.getStyle()
                        .set("background", "white")
                        .set("border", "1px solid #eef0f2")
                        .set("border-radius", "8px")
                        .set("padding", "0.8rem")
                        .set("margin-bottom", "0.5rem");

                String estrellas = "⭐".repeat(Math.max(0, Math.min(5, r.getRating())));
                card.add(new Html("<span><b style='color:#f39c12'>" + estrellas + "</b></span>"));
                card.add(new Paragraph(r.getComentario()));
                reviewsLayout.add(card);
            }
        }
    }

    private void refreshGrid() {
        grid.setItems(juegoService.listarTodos());
    }

    private void clearForm() {
        grid.asSingleSelect().clear();
        populateForm(null);
    }

    private void saveJuego() {
        try {
            if (this.juegoSeleccionado == null) this.juegoSeleccionado = new Juego();
            binder.writeBean(this.juegoSeleccionado);
            juegoService.guardar(this.juegoSeleccionado);
            refreshGrid();
            Notification.show("¡Juego guardado!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            clearForm();
        } catch (Exception e) {
            Notification.show("Error al guardar").addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteJuego() {
        if (this.juegoSeleccionado != null && this.juegoSeleccionado.getId() != null) {
            juegoService.eliminar(this.juegoSeleccionado.getId());
            refreshGrid();
            Notification.show("Eliminado").addThemeVariants(NotificationVariant.LUMO_CONTRAST);
            clearForm();
        }
    }
}