package com.example.proyectoJuegos.Views;

import com.example.proyectoJuegos.Entities.Genero;
import com.example.proyectoJuegos.Entities.Juego;
import com.example.proyectoJuegos.Services.GeneroService;
import com.example.proyectoJuegos.Services.JuegoService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.Lumo;
import jakarta.annotation.security.PermitAll;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@PageTitle("GameHub | Galería")
@Route("")
@AnonymousAllowed
public class MainView extends VerticalLayout {

    private final JuegoService juegoService;
    private final GeneroService generoService;

    // Diálogo y Binder específicos para CREACIÓN
    private final Dialog creationDialog2 = new Dialog();
    private final Binder<Juego> creationBinder2 = new Binder<>(Juego.class);

    // Campos específicos para el formulario de CREACIÓN (locales para que no se mezclen)
    private final TextField cTitulo = new TextField("Título");
    private final DatePicker cFecha = new DatePicker("Fecha Lanzamiento");
    private final TextField cUrl = new TextField("URL Portada");
    private final MultiSelectComboBox<Genero> cGeneros = new MultiSelectComboBox<>("Géneros");
    private final TextArea cDescripcion = new TextArea("Descripción");

    private final VerticalLayout reviewsContainer = new VerticalLayout();
    private final Image headerImage = new Image();

    private final FlexLayout cardContainer = new FlexLayout();

    private final TextField filterName = new TextField();
    private final ComboBox<Genero> filterGenre = new ComboBox<>();
    private final DatePicker filterDate = new DatePicker();

    private final Dialog editDialog = new Dialog();
    private final Binder<Juego> binder = new Binder<>(Juego.class);

    private final Dialog creationDialog = new Dialog();
    private final Binder<Juego> creationBinder = new Binder<>(Juego.class);

    private Juego juegoActual;

    private final TextField titulo = new TextField("Título");
    private final TextField urlImagen = new TextField("URL Portada");
    private final DatePicker fechaSalida = new DatePicker("Fecha Lanzamiento");
    private final MultiSelectComboBox<Genero> generosField = new MultiSelectComboBox<>("Géneros");
    private final TextArea descripcion = new TextArea("Descripción");

    public MainView(JuegoService juegoService, GeneroService generoService) {
        this.juegoService = juegoService;
        this.generoService = generoService;

        addClassName("main-view");
        setSizeFull();
        setupLayout();
        setupDialog();
        setupCreationDialog();
        setupBinder();
        updateGallery();
    }

    private void setupLayout() {
        H1 logo = new H1("GameHub Manager");
        logo.getStyle().set("font-size", "1.6rem").set("margin", "0");

        Button toggleDark = new Button(new Icon(VaadinIcon.MOON), e -> {
            var themeList = UI.getCurrent().getElement().getThemeList();
            if (themeList.contains(Lumo.DARK)) {
                themeList.remove(Lumo.DARK);
            } else {
                themeList.add(Lumo.DARK);
            }
        });

        // BUSCA ESTO EN setupLayout:
        Button addBtn = new Button("Nuevo Juego", new Icon(VaadinIcon.PLUS), e -> {
            // CAMBIA ESTO:
            creationBinder2.readBean(new Juego()); // Limpia el formulario de creación
            creationDialog2.open();               // Abre el diálogo pequeño (creationDialog)
        });

        HorizontalLayout header = new HorizontalLayout(new Icon(VaadinIcon.GAMEPAD), logo, toggleDark, addBtn);
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.expand(logo);
        header.setPadding(true);
        header.getStyle().set("border-bottom", "1px solid var(--lumo-contrast-10pct)");

        filterName.setPlaceholder("Buscar por nombre...");
        filterName.setValueChangeMode(ValueChangeMode.LAZY);
        filterName.addValueChangeListener(e -> updateGallery());
        filterName.setPrefixComponent(VaadinIcon.SEARCH.create());

        filterGenre.setPlaceholder("Categoría");
        filterGenre.setItems(generoService.listarTodos());
        filterGenre.setItemLabelGenerator(Genero::getNombre);
        filterGenre.addValueChangeListener(e -> updateGallery());
        filterGenre.setClearButtonVisible(true);

        filterDate.setPlaceholder("Fecha");
        filterDate.addValueChangeListener(e -> updateGallery());
        filterDate.setClearButtonVisible(true);

        HorizontalLayout toolbar = new HorizontalLayout(filterName, filterGenre, filterDate);
        toolbar.setWidthFull();
        toolbar.setPadding(true);

        cardContainer.setWidthFull();
        cardContainer.getStyle().set("flex-wrap", "wrap").set("gap", "20px").set("justify-content", "center");

        Scroller scroller = new Scroller(cardContainer);
        scroller.setSizeFull();

        add(header, toolbar, scroller);
    }

    private void updateGallery() {
        cardContainer.removeAll();
        List<Juego> todos = juegoService.listarTodos();

        List<Juego> filtrados = todos.stream().filter(j -> {
            boolean nameMatch = filterName.getValue() == null || j.getTitulo().toLowerCase().contains(filterName.getValue().toLowerCase());
            boolean genreMatch = filterGenre.getValue() == null || (j.getGeneros() != null && j.getGeneros().stream().anyMatch(g -> g.getId().equals(filterGenre.getValue().getId())));
            boolean dateMatch = filterDate.getValue() == null || (j.getFechaSalida() != null && j.getFechaSalida().equals(filterDate.getValue()));
            return nameMatch && genreMatch && dateMatch;
        }).collect(Collectors.toList());

        for (Juego juego : filtrados) {
            cardContainer.add(createCard(juego));
        }
    }

    private Component createCard(Juego juego) {
        Div card = new Div();
        card.addClassName("game-card");
        card.getStyle()
                .set("width", "280px")
                .set("border-radius", "12px")
                .set("box-shadow", "var(--lumo-box-shadow-m)")
                .set("background", "var(--lumo-base-color)")
                .set("cursor", "pointer")
                .set("overflow", "hidden")
                .set("transition", "transform 0.2s");

        card.getElement().addEventListener("click", e -> openEditor(juego));
        card.getElement().addEventListener("mouseover", e -> card.getStyle().set("transform", "scale(1.03)"));
        card.getElement().addEventListener("mouseout", e -> card.getStyle().set("transform", "scale(1)"));

        String imgUrl = (juego.getUrlImagen() == null || juego.getUrlImagen().isEmpty()) ? "https://via.placeholder.com/300x180" : juego.getUrlImagen();
        Image img = new Image(imgUrl, "Portada");
        img.setWidthFull();
        img.setHeight("160px");
        img.getStyle().set("object-fit", "cover");

        VerticalLayout info = new VerticalLayout();
        info.setSpacing(false);
        info.setPadding(true);

        H4 title = new H4(juego.getTitulo());
        title.getStyle().set("margin", "0");

        Span date = new Span(juego.getFechaSalida() != null ? juego.getFechaSalida().toString() : "Sin fecha");
        date.getStyle().set("font-size", "var(--lumo-font-size-s)").set("color", "var(--lumo-secondary-text-color)");

        info.add(title, date);
        card.add(img, info);
        return card;
    }



    private void setupDialog() {
        editDialog.setWidth("1050px");
        editDialog.setHeight("80vh");

        generosField.setItems(generoService.listarTodos());
        generosField.setItemLabelGenerator(Genero::getNombre);
        generosField.setPlaceholder("Selecciona géneros...");

        headerImage.setWidthFull();
        headerImage.setHeight("280px");
        headerImage.getStyle()
                .set("object-fit", "cover")
                .set("border-radius", "12px 12px 0 0")
                .set("margin-bottom", "10px");

        HorizontalLayout bodyLayout = new HorizontalLayout();
        bodyLayout.setSizeFull();
        bodyLayout.setSpacing(true);
        bodyLayout.setPadding(true);

        VerticalLayout leftSection = new VerticalLayout();
        leftSection.setWidth("45%");
        leftSection.setSpacing(true);

        H3 reviewTitle = new H3("Opiniones de Jugadores");
        reviewTitle.getStyle().set("margin-top", "0").set("color", "var(--lumo-primary-text-color)");

        reviewsContainer.setWidthFull();
        reviewsContainer.setPadding(false);
        Scroller reviewScroller = new Scroller(reviewsContainer);
        reviewScroller.setHeight("450px"); // Espacio generoso para leer
        reviewScroller.setWidthFull();

        leftSection.add(reviewTitle, reviewScroller);

        VerticalLayout rightSection = new VerticalLayout();
        rightSection.setWidth("55%");
        rightSection.setSpacing(true);

        H3 editTitle = new H3("Editar Información");
        editTitle.getStyle().set("margin-top", "0");

        FormLayout form = new FormLayout();
        form.add(titulo, fechaSalida, urlImagen, generosField, descripcion);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        descripcion.setMinHeight("150px");
        descripcion.setMaxHeight("300px");

        rightSection.add(editTitle, form);

        bodyLayout.add(leftSection, rightSection);

        VerticalLayout dialogLayout = new VerticalLayout(headerImage, bodyLayout);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);

        editDialog.add(new Scroller(dialogLayout));

        Button saveBtn = new Button("Guardar Cambios", e -> save());
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button deleteBtn = new Button("Borrar Juego", e -> delete());
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

        Button closeBtn = new Button("Cancelar", e -> editDialog.close());

        editDialog.getFooter().add(deleteBtn, closeBtn, saveBtn);
    }

    private void setupCreationDialog() {
        creationDialog2.setHeaderTitle("Añadir Nuevo Juego");
        creationDialog2.setWidth("550px");

        cGeneros.setItems(generoService.listarTodos());
        cGeneros.setItemLabelGenerator(Genero::getNombre);
        cGeneros.setPlaceholder("Selecciona géneros...");

        FormLayout form = new FormLayout(cTitulo, cFecha, cUrl, cGeneros, cDescripcion);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        cDescripcion.setMinHeight("120px");

        // USAMOS creationBinder2 en todo el método para que coincida con el botón
        creationBinder2.forField(cGeneros)
                .bind(
                        juego -> new HashSet<>(juego.getGeneros()),
                        (juego, setGeneros) -> juego.setGeneros(new ArrayList<>(setGeneros))
                );

        creationBinder2.forField(cTitulo).asRequired("Obligatorio").bind(Juego::getTitulo, Juego::setTitulo);
        creationBinder2.forField(cFecha).asRequired("Obligatorio").bind(Juego::getFechaSalida, Juego::setFechaSalida);
        creationBinder2.forField(cUrl).bind(Juego::getUrlImagen, Juego::setUrlImagen);
        creationBinder2.forField(cDescripcion).bind(Juego::getDescripcion, Juego::setDescripcion);

        creationDialog2.removeAll(); // Evita duplicados
        creationDialog2.add(form);

        Button saveBtn = new Button("Crear Juego", e -> {
            Juego nuevo = new Juego();
            // Ahora sí lee del binder correcto
            if (creationBinder2.writeBeanIfValid(nuevo)) {
                juegoService.guardar(nuevo);
                updateGallery();
                creationDialog2.close();
                Notification.show("¡Juego añadido con éxito!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                Notification.show("Por favor, rellena los campos obligatorios")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("Cancelar", e -> creationDialog2.close());
        creationDialog2.getFooter().removeAll();
        creationDialog2.getFooter().add(cancelBtn, saveBtn);
    }

    private void setupBinder() {
        binder.forField(generosField)
                .withConverter(
                        set -> (List<Genero>) (set == null ? new ArrayList<>() : new ArrayList<>(set)),
                        list -> (Set<Genero>) (list == null ? new HashSet<>() : new HashSet<>(list))
                )
                .bind(Juego::getGeneros, Juego::setGeneros);
        binder.bindInstanceFields(this);
    }

    private void openEditor(Juego juego) {
        this.juegoActual = juego;
        binder.readBean(juegoActual);

        headerImage.setSrc(juego.getUrlImagen() != null && !juego.getUrlImagen().isEmpty()
                ? juego.getUrlImagen() : "https://via.placeholder.com/1050x280?text=Sin+Imagen");

        reviewsContainer.removeAll();
        if (juego.getReviews() == null || juego.getReviews().isEmpty()) {
            Span noReviews = new Span("Nadie ha reseñado este juego todavía. ¡Sé el primero!");
            noReviews.getStyle().set("color", "gray").set("font-style", "italic");
            reviewsContainer.add(noReviews);
        } else {
            juego.getReviews().forEach(review -> {
                Div card = new Div();
                card.getStyle()
                        .set("background", "var(--lumo-contrast-5pct)")
                        .set("padding", "15px")
                        .set("border-radius", "10px")
                        .set("margin-bottom", "10px")
                        .set("width", "95%");

                HorizontalLayout top = new HorizontalLayout();
                top.setJustifyContentMode(JustifyContentMode.BETWEEN);
                top.setWidthFull();

                Span autor = new Span(VaadinIcon.USER.create(), new Span(" " + review.getAutor().getNombre()));
                autor.getStyle().set("font-weight", "bold");

                Span estrellas = new Span("⭐ ".repeat(review.getRating()));

                top.add(autor, estrellas);

                Paragraph texto = new Paragraph(review.getComentario());
                texto.getStyle().set("margin-top", "8px").set("font-size", "0.9rem");

                card.add(top, texto);
                reviewsContainer.add(card);
            });
        }

        editDialog.open();
    }

    private void save() {
        try {
            binder.writeBean(juegoActual);
            juegoService.guardar(juegoActual);
            updateGallery();
            editDialog.close();
            Notification.show("¡Éxito al guardar!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            Notification.show("Error: Verifique los campos y la URL").addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void delete() {
        if (juegoActual != null && juegoActual.getId() != null) {
            juegoService.eliminar(juegoActual.getId());
            updateGallery();
            editDialog.close();
            Notification.show("Juego eliminado").addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        }
    }
}