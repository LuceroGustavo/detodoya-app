package com.detodoya.entity;

import com.detodoya.enums.Genero;
import com.detodoya.enums.Talle;
import com.detodoya.enums.Temporada;
import com.detodoya.enums.TipoProducto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(exclude = "images")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "p_id")
    private Integer pId;

    @NotBlank(message = "El nombre del producto es requerido")
    @Column(name = "name", nullable = false)
    private String name;


    @Column(name = "medidas", nullable = true)
    private String medidas; // Para indumentaria: medidas de la prenda

    @Column(name = "color")
    private String color; // Campo legacy para compatibilidad
    
    // Relación con Color (Many-to-One) - Color principal
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id")
    private Color colorEntity;
    
    // Relación Many-to-Many con Colores (colores disponibles)
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "product_colors",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "color_id")
    )
    private List<Color> colores = new ArrayList<>();
    
    // Talles disponibles (enum)
    @ElementCollection(targetClass = Talle.class, fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "product_talles", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "talle")
    private List<Talle> talles = new ArrayList<>();

    @Positive(message = "El precio debe ser positivo")
    @Column(name = "price", nullable = false)
    private Double price;

    @PositiveOrZero(message = "La cantidad debe ser cero o positiva")
    @Column(name = "qty", nullable = true)
    private Integer qty;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;


    // Campos adicionales para indumentaria
    @Column(name = "material")
    private String material;

    @Column(name = "cuidados", columnDefinition = "TEXT")
    private String cuidados;

    // Temporadas múltiples (enum)
    @ElementCollection(targetClass = Temporada.class, fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "product_temporadas", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "temporada")
    private List<Temporada> temporadas = new ArrayList<>();

    // Géneros múltiples (enum)
    @ElementCollection(targetClass = Genero.class, fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "product_generos", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "genero")
    private List<Genero> generos = new ArrayList<>();

    @Column(name = "edad_recomendada")
    private String edadRecomendada;

    @Column(name = "tallas_disponibles")
    private String tallasDisponibles; // S,M,L,XL o 38,40,42,44

    @Column(name = "colores_disponibles")
    private String coloresDisponibles; // Rojo,Azul,Verde

    // Campos genéricos para diferentes tipos de productos
    @Column(name = "especificaciones", columnDefinition = "TEXT", nullable = true)
    private String especificaciones; // Para electrónica/hogar: especificaciones técnicas
    
    @Column(name = "marca", nullable = true, length = 100)
    private String marca; // Marca del producto (electrónica, automotor, etc.)
    
    @Column(name = "modelo", nullable = true, length = 100)
    private String modelo; // Modelo del producto (electrónica, automotor, etc.)
    
    @Column(name = "garantia", nullable = true, length = 100)
    private String garantia; // Garantía del producto (ej: "12 meses", "6 meses")
    
    // Campo para tipo de producto (opcional)
    @Column(name = "tipo_producto")
    @Enumerated(EnumType.STRING)
    private TipoProducto tipoProducto; // Tipo de producto para mostrar campos condicionales
    
    // Campos para integración con marketplaces
    @Column(name = "codigo_producto", nullable = true, length = 100)
    private String codigoProducto; // Código SKU o referencia del vendedor
    
    @Column(name = "link_venta", nullable = true, length = 500)
    private String linkVenta; // Link a Facebook Marketplace, MercadoLibre, WhatsApp, etc.
    
    @Column(name = "contacto_vendedor", nullable = true, length = 200)
    private String contactoVendedor; // WhatsApp, email, teléfono del vendedor
    
    @Column(name = "ubicacion", nullable = true, length = 200)
    private String ubicacion; // Ubicación del vendedor o del producto

    // Campos para libros
    @Column(name = "autor", nullable = true, length = 200)
    private String autor; // Autor del libro
    
    @Column(name = "editorial", nullable = true, length = 200)
    private String editorial; // Editorial del libro
    
    @Column(name = "isbn", nullable = true, length = 20)
    private String isbn; // ISBN del libro
    
    @Column(name = "paginas", nullable = true)
    private Integer paginas; // Número de páginas del libro
    
    // Campos genéricos para dimensiones y peso
    @Column(name = "peso", nullable = true, length = 100)
    private String peso; // Peso del producto (ej: "500g", "2.5kg", "1.2 libras")
    
    @Column(name = "dimensiones", nullable = true, length = 255)
    private String dimensiones; // Dimensiones del producto (ej: "20x15x5 cm", "200x80x75 cm")
    
    // Campos para electrónica/electrodomésticos
    @Column(name = "potencia", nullable = true, length = 50)
    private String potencia; // Potencia (ej: "220V", "50W", "1000W")
    
    @Column(name = "consumo", nullable = true, length = 100)
    private String consumo; // Consumo energético (ej: "220V, 50W", "Clase A+")

    @Column(name = "es_destacado")
    private Boolean esDestacado = false;

    @Column(name = "es_nuevo")
    private Boolean esNuevo = false;

    @Column(name = "etiqueta_promocional", length = 50)
    private String etiquetaPromocional;

    @Column(name = "descuento_porcentaje")
    private Double descuentoPorcentaje = 0.0;

    @Column(name = "precio_original")
    private Double precioOriginal;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    // Relación con imágenes/videos unificados
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();
    
    // Relación con videos (legacy - mantener para compatibilidad)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ProductVideo> videos = new ArrayList<>();
    
    // Relación con categorías (Many-to-Many)
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "product_categories",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories = new ArrayList<>();
    
    // Relación con subcategorías (Many-to-Many) - OPCIONAL
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "product_subcategorias",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "subcategoria_id")
    )
    private List<Subcategoria> subcategorias = new ArrayList<>();
    

    // Método para obtener la imagen principal
    public ProductImage getImagenPrincipal() {
        return images.stream()
                .filter(img -> img.getIsPrimary() != null && img.getIsPrimary())
                .findFirst()
                .orElse(images.isEmpty() ? null : images.get(0));
    }

    // Método para obtener la URL de la imagen principal
    public String getImagenPrincipalUrl() {
        ProductImage imagenPrincipal = getImagenPrincipal();
        return imagenPrincipal != null ? imagenPrincipal.getImageUrl() : "/images/no-image.svg";
    }
    
    // Método para verificar si la imagen principal es un video
    public Boolean getImagenPrincipalIsVideo() {
        ProductImage imagenPrincipal = getImagenPrincipal();
        return imagenPrincipal != null ? imagenPrincipal.getIsVideo() : false;
    }

    // Método para obtener el precio con descuento
    public Double getPrecioFinal() {
        if (descuentoPorcentaje > 0) {
            return price * (1 - descuentoPorcentaje / 100);
        }
        return price;
    }

    // Método para verificar si tiene descuento
    public Boolean tieneDescuento() {
        return descuentoPorcentaje > 0;
    }

    // Método para agregar imagen
    public void agregarImagen(ProductImage imagen) {
        images.add(imagen);
        imagen.setProduct(this);
    }

    // Método para remover imagen
    public void removerImagen(ProductImage imagen) {
        images.remove(imagen);
        imagen.setProduct(null);
    }
    
    // Método para obtener el color normalizado
    public String getColorNormalizado() {
        return colorEntity != null ? colorEntity.getName() : color;
    }
    
    // Método para obtener el código hexadecimal del color
    public String getColorHex() {
        return colorEntity != null ? colorEntity.getHexCodeOrDefault() : "#6c757d";
    }
    
    // Métodos para manejar categorías múltiples
    public void agregarCategoria(Category categoria) {
        if (!categories.contains(categoria)) {
            categories.add(categoria);
        }
    }
    
    public void removerCategoria(Category categoria) {
        categories.remove(categoria);
    }
    
    public boolean tieneCategoria(Category categoria) {
        return categories.contains(categoria);
    }
    
    public String getCategoriasComoTexto() {
        return categories.stream()
                .map(Category::getName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("Sin categorías");
    }
    
    // Métodos para manejar subcategorías
    public void agregarSubcategoria(Subcategoria subcategoria) {
        if (!subcategorias.contains(subcategoria)) {
            subcategorias.add(subcategoria);
        }
    }
    
    public void removerSubcategoria(Subcategoria subcategoria) {
        subcategorias.remove(subcategoria);
    }
    
    public boolean tieneSubcategoria(Subcategoria subcategoria) {
        return subcategorias.contains(subcategoria);
    }
    
    public String getSubcategoriasComoTexto() {
        return subcategorias.stream()
                .map(Subcategoria::getName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("Sin subcategorías");
    }
    
    // Método para obtener la categoría principal (primera de la lista)
    public Category getCategoriaPrincipal() {
        return !categories.isEmpty() ? categories.get(0) : null;
    }
    
    // Métodos para manejar temporadas múltiples
    public void agregarTemporada(Temporada temporada) {
        if (!temporadas.contains(temporada)) {
            temporadas.add(temporada);
        }
    }
    
    public void removerTemporada(Temporada temporada) {
        temporadas.remove(temporada);
    }
    
    public boolean tieneTemporada(Temporada temporada) {
        return temporadas.contains(temporada);
    }
    
    public String getTemporadasComoTexto() {
        return temporadas.stream()
                .map(Temporada::getDisplayName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("Sin temporada");
    }
    
    // Métodos para manejar géneros múltiples
    public void agregarGenero(Genero genero) {
        if (!generos.contains(genero)) {
            generos.add(genero);
        }
    }
    
    public void removerGenero(Genero genero) {
        generos.remove(genero);
    }
    
    public boolean tieneGenero(Genero genero) {
        return generos.contains(genero);
    }
    
    public String getGenerosComoTexto() {
        return generos.stream()
                .map(Genero::getDisplayName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("Sin género");
    }
    
    // Métodos para manejar talles múltiples
    public void agregarTalle(Talle talle) {
        if (!talles.contains(talle)) {
            talles.add(talle);
        }
    }
    
    public void removerTalle(Talle talle) {
        talles.remove(talle);
    }
    
    public boolean tieneTalle(Talle talle) {
        return talles.contains(talle);
    }
    
    public String getTallesComoTexto() {
        return talles.stream()
                .map(Talle::getDisplayName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("Sin talle");
    }

    // Getters y Setters manuales (por si Lombok no funciona)
    public Integer getPId() { return pId; }
    public void setPId(Integer pId) { this.pId = pId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    
    public String getMedidas() { return medidas; }
    public void setMedidas(String medidas) { this.medidas = medidas; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }
    
    public String getCuidados() { return cuidados; }
    public void setCuidados(String cuidados) { this.cuidados = cuidados; }
    
    public List<Temporada> getTemporadas() { return temporadas; }
    public void setTemporadas(List<Temporada> temporadas) { this.temporadas = temporadas; }
    
    public List<Genero> getGeneros() { return generos; }
    public void setGeneros(List<Genero> generos) { this.generos = generos; }
    
    public String getEdadRecomendada() { return edadRecomendada; }
    public void setEdadRecomendada(String edadRecomendada) { this.edadRecomendada = edadRecomendada; }
    
    public String getTallasDisponibles() { return tallasDisponibles; }
    public void setTallasDisponibles(String tallasDisponibles) { this.tallasDisponibles = tallasDisponibles; }
    
    public String getColoresDisponibles() { return coloresDisponibles; }
    public void setColoresDisponibles(String coloresDisponibles) { this.coloresDisponibles = coloresDisponibles; }
    
    public Boolean getEsDestacado() { return esDestacado; }
    public void setEsDestacado(Boolean esDestacado) { this.esDestacado = esDestacado; }
    
    public Boolean getEsNuevo() { return esNuevo; }
    public void setEsNuevo(Boolean esNuevo) { this.esNuevo = esNuevo; }
    
    public String getEtiquetaPromocional() { return etiquetaPromocional; }
    public void setEtiquetaPromocional(String etiquetaPromocional) { this.etiquetaPromocional = etiquetaPromocional; }
    
    public Double getDescuentoPorcentaje() { return descuentoPorcentaje; }
    public void setDescuentoPorcentaje(Double descuentoPorcentaje) { this.descuentoPorcentaje = descuentoPorcentaje; }
    
    public Double getPrecioOriginal() { return precioOriginal; }
    public void setPrecioOriginal(Double precioOriginal) { this.precioOriginal = precioOriginal; }
    
    // Getters y setters para campos genéricos
    public String getEspecificaciones() { return especificaciones; }
    public void setEspecificaciones(String especificaciones) { this.especificaciones = especificaciones; }
    
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    
    public String getGarantia() { return garantia; }
    public void setGarantia(String garantia) { this.garantia = garantia; }
    
    public TipoProducto getTipoProducto() { return tipoProducto; }
    public void setTipoProducto(TipoProducto tipoProducto) { this.tipoProducto = tipoProducto; }
    
    // Getters y setters para campos de integración marketplace
    public String getCodigoProducto() { return codigoProducto; }
    public void setCodigoProducto(String codigoProducto) { this.codigoProducto = codigoProducto; }
    
    public String getLinkVenta() { return linkVenta; }
    public void setLinkVenta(String linkVenta) { this.linkVenta = linkVenta; }
    
    public String getContactoVendedor() { return contactoVendedor; }
    public void setContactoVendedor(String contactoVendedor) { this.contactoVendedor = contactoVendedor; }
    
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    
    // Getters y setters para campos de libros
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    
    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public Integer getPaginas() { return paginas; }
    public void setPaginas(Integer paginas) { this.paginas = paginas; }
    
    // Getters y setters para campos genéricos (peso y dimensiones)
    public String getPeso() { return peso; }
    public void setPeso(String peso) { this.peso = peso; }
    
    public String getDimensiones() { return dimensiones; }
    public void setDimensiones(String dimensiones) { this.dimensiones = dimensiones; }
    
    // Getters y setters para campos de electrónica
    public String getPotencia() { return potencia; }
    public void setPotencia(String potencia) { this.potencia = potencia; }
    
    public String getConsumo() { return consumo; }
    public void setConsumo(String consumo) { this.consumo = consumo; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    
    public List<ProductImage> getImages() { return images; }
    public void setImages(List<ProductImage> images) { this.images = images; }
    
    // Getters y setters para las nuevas relaciones
    public List<Color> getColores() { return colores; }
    public void setColores(List<Color> colores) { this.colores = colores; }
    
    public List<Talle> getTalles() { return talles; }
    public void setTalles(List<Talle> talles) { this.talles = talles; }
    
    
    public List<Category> getCategories() { return categories; }
    public void setCategories(List<Category> categories) { this.categories = categories; }
    
    public List<Subcategoria> getSubcategorias() { return subcategorias; }
    public void setSubcategorias(List<Subcategoria> subcategorias) { this.subcategorias = subcategorias; }
    
    public Color getColorEntity() { return colorEntity; }
    public void setColorEntity(Color colorEntity) { this.colorEntity = colorEntity; }
}

