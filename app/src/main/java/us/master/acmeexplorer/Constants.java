package us.master.acmeexplorer;

import java.util.List;

import us.master.acmeexplorer.entity.Trip;

public class Constants {

    /*
     *  ----------------------------GENERATE TRIPS CONSTANTS ------------------------------------
     */
    public final static String[] ciudades={"Tirana","Berlín","Andorra La Vieja","Ereván","Viena","Bakú","Bruselas",
            "Minsk","Sarajevo","Sofía","Praga","Zagreb","Copenhague","Bratislava","Lublijana","Madrid","Tallín",
            "Helsinki","París","Tiflis","Atenas","Budapest","Dublín","Reikiavik","Roma","Riga","Vaduz","Vilna",
            "Luxemburgo","Skopje","La Valeta","Chisinau","Mónaco","Podgorica","Oslo","Amsterdam","Varsovia",
            "Lisboa","Londres","Bucarest","Moscú","Ciudad De San Marino","Belgrado","Estocolmo","Berna","Kiev",
            "Ciudad Del Vaticano"};
    public final static String[] lugarSalida={"Sevilla","Málaga","Faro","Barcelona","Madrid","Valencia"};
    public final static String[] adjetivo = {"Fascinante", "Increíble", "Gran", "Buen", "Precioso"};
    public final static String[] urlImagenes={"https://png.pngtree.com/element_pic/17/09/23/891e71ffa7e5efe9f5440513fa069add.jpg",
            "https://png.pngtree.com/element_pic/17/04/20/e29789b631107bd82df67d3f46112f0e.jpg",
            "https://png.pngtree.com/element_pic/16/09/12/2357d6c812acf90.jpg",
            "https://png.pngtree.com/element_pic/20/16/01/3156adb71123719.jpg",
            "https://png.pngtree.com/element_pic/30/03/20/1656fbd4b4641fc.jpg",
            "https://png.pngtree.com/element_pic/00/00/00/0056a3602a2cf41.jpg",
            "https://png.pngtree.com/element_our/sm/20180416/sm_5ad452dbaaf09.png"};
    public static List<Trip> TRIPS = null;
    public final static String DEFAULT_TRIP_PICTURE = "https://www.ruizre.es/wp-content/uploads/2018/07/1524624771669.jpg";

    public static final String IntentViaje ="Viaje" ;

    /*
     *  ----------------------------SHARED PREFERENCES CONSTANTS ------------------------------------
     */

    public final static String filtroPreferences = "Filtro";
    public final static String fechaInicio = "FechaInicio";
    public final static String fechaFin = "FechaFin";
    public final static String maxPrice = "MaxPrice";
    public final static String minPrice = "MinPrice";


    /**
     * --------------------------- INTENT EXTRA CONSTANTS ------------------------------------------
     */

    public final static String USER_PRINCIPAL = "userPrincipal";
}
