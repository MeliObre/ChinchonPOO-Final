package juego.modelo;

import java.util.*;

public class Mano extends ConjuntoCartas{

    public boolean esCerrable (){

        return this.esManoCerrable().esCerrable;
    }

    public int cerrarMano (){
        ResultCierre resultadoCierre = this.esManoCerrable();
        if (!resultadoCierre.esCerrable)
            throw new Error("La mano es cerrable.");
        if (resultadoCierre.cartaCorte != null){
            this.quitarCarta(resultadoCierre.cartaCorte);
        }
        return calcularPuntajeRestante();
    }

    public int calcularPuntajeRestante(){
        if (esChinchon())
            return -1000;
        int puntosPorSobra = this.sumarPuntosCartasNoUsadas();
        if (puntosPorSobra == 0)
            return -10;
        return puntosPorSobra;
    }

    //metodo para sumar el puntaje
    public int sumarPuntosCartasNoUsadas() {
        // obtengo las cartas que quedaron sueltas
        List<Carta> noUsadas = this.cartasNoUsadas(this.getCopiaArrayList());

        //sumo los valores usando el metodo privado corregido (obtenerValorPuntaje)
        return noUsadas.stream()
                .mapToInt(this::obtenerValorPuntaje)
                .sum();
    }

    public boolean esChinchon(){
        this.ordenarCarta();
        Carta cartaAnterior = this.getCarta(1);
        for (int i = 2; i <= this.getCantidadCartas(); i++){
            Carta cartaActual = this.getCarta(i);

            if (cartaActual.getPalo() != cartaAnterior.getPalo())
                return false;

            if ((cartaAnterior.getNumero() + 1) != cartaActual.getNumero()){
                return false;
            }
            cartaAnterior = cartaActual;
        }

        return true;
    }

    public String toString (){
        String textoSalida= "";
        for (int i = 1; i <= this.getCantidadCartas(); i++){
            textoSalida = textoSalida+String.valueOf(i)+ " . ";
            textoSalida = textoSalida+ this.getCarta(i).toString();
            textoSalida = textoSalida + "\n";
        }
        return textoSalida;
    }

    public static Mano arrayToMano(List<Carta> cartas) {
        Mano resultado = new Mano();
        for (int i = 0; i < cartas.size(); i++) {
            resultado.anadirCarta(cartas.get(i));
        }
        return resultado;
    }

    private  int obtenerValorPuntaje(Carta carta) {
        if (carta.getPalo() == Palo.COMODIN) {
            return 25; // penalizacion por comodin
        }

        int numero = carta.getNumero();

        // Las figuras (10, 11, 12) valen 10 puntos, no su numero.
        if (numero >= 10 && numero <= 12) {
            return 10;
        }
        return numero; // 1, 2, 3... 9 valen su numero (inicialmente, mas adelante tengo que sacar los 8 y9)
    }

    private List<Carta> buscarCombinacion(List<TipoJuego> grupos, int objetivo, List<Carta> usadas,
                                          int indiceInicial) {
        // ordenar por tamaño descendente (usando getCartas().size())
        if (indiceInicial == 0) {
            grupos.sort((a, b) -> b.getCartas().size() - a.getCartas().size());
        }

        if (usadas.size() == objetivo)
            return usadas;
        if (indiceInicial >= grupos.size() || usadas.size() > objetivo)
            return null;

        for (int i = indiceInicial; i < grupos.size(); i++) {
            //ahora el grupo es de tipo TipoJuego (Trio o Escalera)
            TipoJuego grupo = grupos.get(i);

            // verifico Solapamiento, usop grupo.getCartas() para acceder a la lista
            boolean haySolapamiento = grupo.getCartas().stream().anyMatch(usadas::contains);
            if (haySolapamiento)
                continue;

            if (usadas.size() + grupo.getCartas().size() > objetivo)
                continue;

            List<Carta> combinadas = new ArrayList<>(usadas);
            // añado las cartas reales usando grupo.getCartas()
            combinadas.addAll(grupo.getCartas());

            List<Carta> resultado = buscarCombinacion(grupos, objetivo, combinadas, i + 1);
            if (resultado != null)
                return resultado;
        }
        return null;
    }

    public  List<Carta> cartasNoUsadas(List<Carta> mano) {
        List<TipoJuego> combinacionesPosibles = new ArrayList<>();
        combinacionesPosibles.addAll(Trio.encontrarEn(mano));
        combinacionesPosibles.addAll(Escalera.encontrarEn(mano));

        List<Carta> cartasUsadas = null;
        //busco la combinacion mas larga >= 3 cartas
        for (int objetivo = mano.size(); objetivo >= 3; objetivo--) {
            cartasUsadas = buscarCombinacion(combinacionesPosibles, objetivo, new ArrayList<>(), 0);
            if (cartasUsadas != null) {
                break;
            }
        }

        if (cartasUsadas != null) {
            List<Carta> cartasNoUsadas = new ArrayList<>(mano);
            cartasNoUsadas.removeAll(cartasUsadas);
            return cartasNoUsadas;
        }

        // Si no encontramos ningun trio ni escalera de ≥3 cartas
        return new ArrayList<>(mano);
    }

    public boolean esListaCerrable (List<Carta> mano){
        //solo para el testeo
        if (mano.size() !=7)
            return false;
        List<TipoJuego> combinacionesPosibles = new ArrayList<>();
        // Dentro de esListaCerrable:
        combinacionesPosibles.addAll(Trio.encontrarEn(mano)); // Pasa la variable mano como argumento
        combinacionesPosibles.addAll(Escalera.encontrarEn(mano));
        if (buscarCombinacion(combinacionesPosibles, 7,new ArrayList<>(),0) != null){
            return true;
        }
        List<Carta> usadaSeis = buscarCombinacion(combinacionesPosibles, 6 , new ArrayList<>(), 0);
        if (usadaSeis != null)
            return true;
        return false;
    }
    public  ResultCierre esManoCerrable (){
        List<Carta> mano = this.getCopiaArrayList();
        ResultCierre resultCierreRecord = null ; //por default devuelve falso

        for (int i = 0 ; i < mano.size(); i++){
            //copia de la mano sin la carta actual
            List<Carta> copia = new ArrayList<>(mano);
            Carta cartaCierre = mano.get(i);
            copia.remove(i);

            if (esListaCerrable(copia)){
                // si una copia cumple la codicion la mano es cerrable
                Mano manoCerrableActual = Mano.arrayToMano(copia);
                if (resultCierreRecord == null){
                    resultCierreRecord = new ResultCierre(true, cartaCierre, manoCerrableActual);
                    continue;
                }
                int puntajeRecord = resultCierreRecord.mano.calcularPuntajeRestante();

                if (manoCerrableActual.calcularPuntajeRestante() < puntajeRecord) {
                    // si no hay ninguna mano cerrable o si la mano cerrable actual da puntos más
                    // bajos que la guardada, actualizo el resultado
                    resultCierreRecord = new ResultCierre(true, cartaCierre, manoCerrableActual);
                }
            }
        }
        if (resultCierreRecord == null)
            return new ResultCierre(false, null, null);

        return resultCierreRecord;
    }
/*
    private  List<List<Carta>> combinar(List<Carta> elementos, int n) {
        if (n > elementos.size())
            return Collections.emptyList();
        if (n == 1) {
            List<List<Carta>> individuales = new ArrayList<>();
            for (Carta c : elementos) {
                individuales.add(Arrays.asList(c));
            }
            return individuales;
        }

        List<List<Carta>> combinaciones = new ArrayList<>();
        for (int i = 0; i <= elementos.size() - n; i++) {
            Carta cabeza = elementos.get(i);
            List<Carta> cola = elementos.subList(i + 1, elementos.size());
            for (List<Carta> sub : combinar(cola, n - 1)) {
                List<Carta> nueva = new ArrayList<>();
                nueva.add(cabeza);
                nueva.addAll(sub);
                combinaciones.add(nueva);
            }
        }
        return combinaciones;
    }

 */

/*
    private List<Escalera> encontrarEscaleras(List<Carta> mano) {
        // Agrupo todas las cartas de la mano por palo para buscar escaleras,
        // ya que una escalera debe ser del mismo palo.
        Map<Palo, List<Carta>> porPalo = new HashMap<>();
        for (Carta carta : mano) { // uso mano
            porPalo.computeIfAbsent(carta.getPalo(), k -> new ArrayList<>()).add(carta);
        }

        // devolvera objetos Escalera
        List<Escalera> escalasValidas = new ArrayList<>();
        // itero sobre cada palo
        for (List<Carta> grupo : porPalo.values()) {
            // uso TreeSet para obtener solo los numeros unicos y ordenados del palo
            Set<Integer> numerosUnicos = new TreeSet<>();
            for (Carta c : grupo)
                numerosUnicos.add(c.getNumero());
            List<Integer> listaNumeros = new ArrayList<>(numerosUnicos);

            for (int i = 0; i < listaNumeros.size(); i++) {
                List<Carta> secuencia = new ArrayList<>();
                // Comienza a iterar desde el número actual para formar una secuencia
                for (int j = i; j < listaNumeros.size(); j++) {
                    // // se rompe si el numero actual no es el consecutivo del anterior
                    if (j > i && listaNumeros.get(j) != listaNumeros.get(j - 1) + 1)
                        break;
                    int num = listaNumeros.get(j);
                    //busca la carta REAL que corresponde a este 'num' y que aún no ha sido usada en esta secuencia.
                    Optional<Carta> opcion = grupo.stream()
                            .filter(c -> c.getNumero() == num && !secuencia.contains(c))
                            .findFirst();
                    if (!opcion.isPresent())
                        break;
                    secuencia.add(opcion.get());

                    // si se encuentra una secuencia valida (>= 3), se crea un objeto Escalera
                    if (secuencia.size() >= 3) {
                        // crea y guarda el objeto Escalera, no una lista de listas
                        escalasValidas.add(new Escalera(new ArrayList<>(secuencia)));
                    }
                }
            }
        }
        return escalasValidas;
    }

    private List<Trio> encontrarTrio(List<Carta> mano) {
       // uso un Map para agrupar todas las cartas por su numero
        Map<Integer, List<Carta>> porNumero = new HashMap<>();
        for (Carta carta : mano) { // USAR 'mano'
            porNumero.computeIfAbsent(carta.getNumero(), k -> new ArrayList<>()).add(carta);
        }

        // cambie el tipo de retorno devolvera objetos Trio
        List<Trio> triosValidos = new ArrayList<>();
        //Iteraro sobre cada grupo de numeros iguales
        for (Map.Entry<Integer, List<Carta>> entrada : porNumero.entrySet()) {
            List<Carta> grupo = entrada.getValue();
            if (grupo.size() >= 3) { //solo sigo si hay almenos 3 cartas del mismo palo

                // usar combinar para obtener todas las posibles combinaciones de 3
                List<List<Carta>> combinacionesDeTres = this.combinar(grupo, 3);
                for (List<Carta> listaCartas : combinacionesDeTres) {
                    triosValidos.add(new Trio(listaCartas)); // Crea y guarda el objeto Trio
                }

                // si hay 4 cartas, tambien es una combinacion valida
                if (grupo.size() == 4) {
                    triosValidos.add(new Trio(new ArrayList<>(grupo))); // Crea el objeto Trio (cuarteto)
                }
            }
        }
        return triosValidos;
    }

 */

}
