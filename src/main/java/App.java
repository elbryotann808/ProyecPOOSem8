import Dao.ProductDAO;
import Model.Product;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class App {
    private static Scanner sc = new Scanner(System.in);
    private static ProductDAO dao = new ProductDAO();

    private static final String CODE_REGEX = "^[A-Za-z]{2}\\d{4}$";
    private static final String STRINGS_REGEX = "^[\\p{L}0-9 ]{3,50}$";
    private static final String STRINGSlONGS_REGEX = "^[\\p{L}\\p{N}\\s\\.,!¡¿?()#\"'$%&*\\-+/\\[\\]\\{\\}@:;]{10,500}$";;
    private static final String COP_REGEX = "^[0-9]+(?:\\.[0-9]{3})*$";
    private static final String INTEGER_REGEX = "^\\d+$";

    private static final String CODE_TEXT = "El codigo tiene que tener 2 letras y 4 digitos, ejem: AS4561. ";
    private static final String NAME_TEXT = "El nombre tiene que contener letras y espacios, sin caracteres especiales. ";
    private static final String DESCRIPTION_TEXT = "La descricion solamente puede contener letras, numeros, espacios y puntuacion basica. ";
    private static final String PRICE_TEXT = "El precio debe ser un numeros en pesos, solo digitos y puntos como separador de miles, ejem: 1500000 o 1.500.000. ";
    private static final String CATEGORY_TEXT = "La categoria deberia de contener solo letras y espacios, nada de caracteres especiales. ";
    private static final String STOCK_TEXT = "La cantidad solo puede tener un numero entero positivo. ";
    private static final String OPT_TEXT= "La opcion solo puede tener un numero entero positivo. ";


    private static String inputValid (Scanner sc, String data, String reg, String errorMsg){
        String input;
        while(true){
            System.out.print(data);
            input = sc.nextLine();
            if (input.matches(reg)) return input;
            System.out.println("[ERROR] " + errorMsg);
        }
    }

    public static void main(String[] args){
        int opt;
        do{
            System.out.println("\nCRUD productos KD-Electronics");
            System.out.println("1. Crear producto");
            System.out.println("2. Buscar producto");
            System.out.println("3. Actualizar producto");
            System.out.println("4. Eliminar producto");
            System.out.println("5. Ver todos los productos");
            System.out.println("6. Actualizar stock de un producto");
            System.out.println("0. Salir");
            opt = Integer.parseInt(inputValid(sc, "Opcion: ", INTEGER_REGEX, OPT_TEXT));


            switch (opt){
                case 1: create(); break;
                case 2: read(); break;
                case 3: update(); break;
                case 4: delete(); break;
                case 5: readAll(); break;
                case 6: updateStock(); break;
            }
        }  while (opt != 0);
    }

    private static void create(){
        String code = inputValid(sc, "Codigo: ", CODE_REGEX, CODE_TEXT);
        String name = inputValid(sc, "Nombre: ", STRINGS_REGEX, NAME_TEXT);
        String desc = inputValid(sc, "Descripcion: ", STRINGSlONGS_REGEX, DESCRIPTION_TEXT);

        String pbStr = inputValid(sc, "Precio base: ", COP_REGEX, PRICE_TEXT);
        BigDecimal pb = new BigDecimal(pbStr.replace(".", ""));

        String pvStr = inputValid(sc, "Precio venta: ", COP_REGEX, PRICE_TEXT);
        BigDecimal pv = new BigDecimal(pvStr.replace(".", ""));



        String cat = inputValid(sc,"Categoria: ", STRINGS_REGEX, CATEGORY_TEXT);
        int sto = Integer.parseInt(inputValid(sc, "Cantidad: ", INTEGER_REGEX, STOCK_TEXT));

        Product p = new Product(code, name, desc, pb, pv , cat, sto);
        try {
            if ( dao.create(p)) System.out.println("Producto creado");
            else System.out.println("Error al crear");
        }catch (SQLException e){
            System.out.println("Error al unitentar crear un nuevo producto");
            e.printStackTrace();
        }
    }

    private static void read(){
        String code = inputValid(sc, "Codigo a buscar: ", CODE_REGEX, CODE_TEXT);
        try {
            Product p = dao.readByCode(code);
            System.out.println(p != null? p: "No encontrado");
        }catch (SQLException e){
            System.out.println("Error al leer el producto con el codigo: " + code);
            e.printStackTrace();
        }
    }

    private static void readAll(){
        try {
            List<Product> lista = dao.readAll();
            lista.forEach(System.out::println);
        }catch (SQLException e){
            System.out.println("Huvo un error al momento de leer todos los productos");
            e.printStackTrace();
        }
    }

    private static void update(){
        String code = inputValid(sc, "Codigo a actualizar: ", CODE_REGEX, CODE_TEXT);
        Product p = null;
        try {
            p = dao.readByCode(code);
        }catch (SQLException e){
            System.out.println("Error al leer el producto para el actualizado");
            e.printStackTrace();
            return;
        }

        if (p == null){
            System.out.println("No existe");
            return;
        }

        String name = inputValid(sc,"Nuevo nombre ("+ p.getName() +"): ", STRINGS_REGEX, NAME_TEXT);
        p.setName(name);

        String desc = inputValid(sc, "Nueva descripcion ("+ p.getDescription() +"): " , STRINGSlONGS_REGEX, DESCRIPTION_TEXT);
        p.setDescription(desc);

        String pbStr = inputValid(sc, "Nuevo precio base ("+ p.getPriceBase()+"): " , COP_REGEX, PRICE_TEXT);
        BigDecimal pb = new BigDecimal(pbStr.replace(".", ""));
        p.setPriceBase(pb);

        String pvStr = inputValid(sc, "Nuevo precio venta ("+ p.getPriceSale() +"): " , COP_REGEX, PRICE_TEXT);
        BigDecimal pv = new BigDecimal(pvStr.replace(".", ""));
        p.setPriceSale(pv);

        String cat = inputValid(sc, "Nueva Categoria ("+ p.getCategory() +"): " , STRINGS_REGEX, CATEGORY_TEXT);
        p.setCategory(cat);

        int sto = Integer.parseInt(inputValid(sc, "Nueva cantidad ("+ p.getStock() +"): " , INTEGER_REGEX, STOCK_TEXT));
        p.setStock(sto);

        try {
            if (dao.update(p)) System.out.println("Actualizado");
            else System.out.println("Error al actualizar");
        }catch (SQLException e){
            System.out.println("Error al intentar actualizar el producto");
            e.printStackTrace();
        }
    }

    private static void updateStock(){
        String code = inputValid(sc, "Codigo del producto: ", CODE_REGEX, CODE_TEXT);
        int amount = Integer.parseInt(inputValid(sc, "Cantidad a agregar: ", INTEGER_REGEX, STOCK_TEXT));
        try {
            if (dao.updateStock(code, amount)){
                System.out.println("El stock aumento de forma correcta");
            }else {
                System.out.println("No se pudo actualizar el stock");
            }
        }catch (SQLException e){
            System.out.println("Error al procesar el aumento ");
            e.printStackTrace();
        }
    }

    private static void delete(){
        String code = inputValid(sc, "Codigo a eliminar: ", CODE_REGEX, CODE_TEXT);
        try {
            if (dao.delete(code)) System.out.println("Eliminado");
            else System.out.println("Error de eliminado");
        }catch (SQLException e){
            System.out.println("Huvo un erro al eliminar un producto. ");
            e.printStackTrace();
        }
    }

}
