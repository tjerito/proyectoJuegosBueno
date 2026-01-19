package com.example.proyectoJuegos.Views;

import com.example.proyectoJuegos.Entities.Juego;
import com.example.proyectoJuegos.Entities.Review;
import com.example.proyectoJuegos.Services.JuegoService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
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
@CssImport("./styles/styles.css") // Importa tu archivo CSS
public class MainView extends VerticalLayout { // Cambiado a VerticalLayout para mejor control de bordes

    private final Grid<Juego> grid = new Grid<>(Juego.class, false);
    private final TextField titulo = new TextField("Título del Videojuego");
    private final DatePicker fechaSalida = new DatePicker("Fecha de Lanzamiento");
    private final TextArea descripcion = new TextArea("Descripción");
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

        // Header de la Aplicación
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
        splitLayout.setOrientation(SplitLayout.Orientation.HORIZONTAL);

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
        editorLayout.setSpacing(true);
        editorLayout.getStyle().set("background-color", "#fcfcfc");

        H3 sectionTitle = new H3("Gestión de Información");
        sectionTitle.getStyle().set("margin-top", "0");

        FormLayout formLayout = new FormLayout();
        titulo.setWidthFull();
        descripcion.setHeight("150px");
        formLayout.add(titulo, fechaSalida, descripcion);
        formLayout.setColspan(descripcion, 2);

        HorizontalLayout buttonLayout = new HorizontalLayout(save, delete, cancel);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        buttonLayout.getStyle().set("margin-top", "1rem");

        Hr divider = new Hr();

        H4 reviewTitle = new H4("Feedback de la Comunidad");
        reviewsLayout.setWidthFull();
        reviewsLayout.setPadding(false);

        editorLayout.add(sectionTitle, formLayout, buttonLayout, divider, reviewTitle, reviewsLayout);
        return editorLayout;
    }

    private void cargarResenasVisuales(List<Review> reviews) {
        reviewsLayout.removeAll();
        if (reviews == null || reviews.isEmpty()) {
            Span empty = new Span("Aún no hay críticas para este título.");
            empty.getStyle().set("color", "gray").set("font-style", "italic");
            reviewsLayout.add(empty);
        } else {
            for (Review r : reviews) {
                Div card = new Div();
                card.setWidthFull();
                card.getStyle()
                        .set("background", "white")
                        .set("border", "1px solid #eef0f2")
                        .set("border-radius", "12px")
                        .set("padding", "1rem")
                        .set("box-shadow", "0 2px 4px rgba(0,0,0,0.05)")
                        .set("margin-bottom", "0.5rem");

                String estrellas = "⭐".repeat(Math.max(0, Math.min(5, r.getRating())));
                Html ratingLabel = new Html("<span><b style='color:#f39c12'>" + estrellas + "</b></span>");

                Paragraph comment = new Paragraph(r.getComentario());
                comment.getStyle().set("margin", "0.5rem 0").set("font-size", "0.95rem");

                Span author = new Span(VaadinIcon.USER.create());
                author.add(" " + (r.getAutor() != null ? r.getAutor().getNombre() : "Anónimo"));
                author.getStyle().set("font-size", "0.8rem").set("color", "#7f8c8d");

                card.add(ratingLabel, comment, author);
                reviewsLayout.add(card);
            }
        }
    }

    private void refreshGrid() {
        grid.setItems(juegoService.listarTodos());
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Juego value) {
        // 1. Guardamos la referencia.
        // Si 'value' es null, significa que se ha deseleccionado la fila.
        this.juegoSeleccionado = value;

        // 2. Leemos los datos en el Binder.
        // Si es null, el binder limpiará automáticamente todos los campos del formulario.
        binder.readBean(this.juegoSeleccionado);

        if (value != null) {
            // 3. Cargar reseñas: Verificamos que la lista no sea null antes de enviarla
            cargarResenasVisuales(value.getReviews());

            // 4. EL ARREGLO DEL ID:
            // El botón eliminar solo debe estar habilitado si el juego ya existe (tiene ID).
            // Si es un "Nuevo Juego", el ID será null y el botón debe estar desactivado.
            boolean esJuegoExistente = value.getId() != null;
            delete.setEnabled(esJuegoExistente);

            // Opcional: Si el juego es nuevo, podemos cambiar el título del editor
            // editorTitle.setText(esJuegoExistente ? "Editando: " + value.getTitulo() : "Nuevo Videojuego");

        } else {
            // Si no hay nada seleccionado, limpiamos el área de reseñas y desactivamos borrar
            reviewsLayout.removeAll();
            delete.setEnabled(false);
        }
    }

    private void saveJuego() {
        try {
            if (this.juegoSeleccionado == null) this.juegoSeleccionado = new Juego();
            binder.writeBean(this.juegoSeleccionado);
            juegoService.guardar(this.juegoSeleccionado);
            refreshGrid();
            Notification notification = Notification.show("¡Juego actualizado!");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            clearForm();
        } catch (Exception e) {
            Notification.show("Error: Revisa los campos").addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteJuego() {
        if (this.juegoSeleccionado != null ) {
            juegoService.eliminar(this.juegoSeleccionado.getId());
            refreshGrid();
            Notification.show("Juego borrado").addThemeVariants(NotificationVariant.LUMO_CONTRAST);
            clearForm();
        }
    }
}