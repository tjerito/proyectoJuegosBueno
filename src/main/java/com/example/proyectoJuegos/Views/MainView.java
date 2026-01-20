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

    // Contenedor de la galería
    private final FlexLayout cardContainer = new FlexLayout();

    // Filtros
    private final TextField filterName = new TextField();
    private final ComboBox<Genero> filterGenre = new ComboBox<>();
    private final DatePicker filterDate = new DatePicker();

    // Diálogo de edición
    private final Dialog editDialog = new Dialog();
    private final Binder<Juego> binder = new Binder<>(Juego.class);
    private Juego juegoActual;

    // Campos del formulario
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
        setupBinder();
        updateGallery();
    }

    private void setupLayout() {
        // --- HEADER CON MODO OSCURO ---
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

        Button addBtn = new Button("Nuevo Juego", new Icon(VaadinIcon.PLUS), e -> openEditor(new Juego()));
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout header = new HorizontalLayout(new Icon(VaadinIcon.GAMEPAD), logo, toggleDark, addBtn);
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.expand(logo);
        header.setPadding(true);
        header.getStyle().set("border-bottom", "1px solid var(--lumo-contrast-10pct)");

        // --- BARRA DE FILTROS ---
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

        // --- CONTENEDOR DE TARJETAS ---
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
        editDialog.setHeaderTitle("Gestionar Videojuego");
        editDialog.setModal(true);
        editDialog.setDraggable(true);
        editDialog.setWidth("500px");

        generosField.setItems(generoService.listarTodos());
        generosField.setItemLabelGenerator(Genero::getNombre);

        FormLayout form = new FormLayout(titulo, fechaSalida, urlImagen, generosField, descripcion);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        Button saveBtn = new Button("Guardar", e -> save());
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        Button deleteBtn = new Button("Eliminar", e -> delete());
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

        Button closeBtn = new Button("Cerrar", e -> editDialog.close());

        editDialog.add(form);
        editDialog.getFooter().add(deleteBtn, closeBtn, saveBtn);
    }

    private void setupBinder() {
        // Conversor Set -> List para géneros
        binder.forField(generosField)
                .withConverter(
                        // Forzamos que devuelva List (la interfaz) y no ArrayList (la clase concreta)
                        set -> (List<Genero>) (set == null ? new ArrayList<>() : new ArrayList<>(set)),
                        // Forzamos que devuelva Set (la interfaz)
                        list -> (Set<Genero>) (list == null ? new HashSet<>() : new HashSet<>(list))
                )
                .bind(Juego::getGeneros, Juego::setGeneros);
        binder.bindInstanceFields(this);
    }

    private void openEditor(Juego juego) {
        this.juegoActual = juego;
        binder.readBean(juegoActual);
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