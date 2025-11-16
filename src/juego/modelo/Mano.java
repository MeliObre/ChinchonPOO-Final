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

    // Nuevo método para sumar el puntaje
    public int sumarPuntosCartasNoUsadas() {
        // 1. Obtiene las cartas que quedaron sueltas
        List<Carta> noUsadas = this.cartasNoUsadas(this.getCopiaArrayList());

        // 2. Suma los valores usando el método privado corregido (obtenerValorPuntaje)
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
            return 25; // O el valor de penalización que uses (ej: 25, 50)
        }

        int numero = carta.getNumero();

        // Las figuras (10, 11, 12) valen 10 puntos, no su número.
        if (numero >= 10 && numero <= 12) {
            return 10;
        }
        return numero; // As, 2, 3... 7 valen su numero
    }

    private List<Carta> buscarCombinacion(List<TipoJuego> grupos, int objetivo, List<Carta> usadas,
                                          int indiceInicial) {
        // 1. Ordenar por tamaño descendente (usando getCartas().size())
        if (indiceInicial == 0) {
            grupos.sort((a, b) -> b.getCartas().size() - a.getCartas().size());
        }

        if (usadas.size() == objetivo)
            return usadas;
        if (indiceInicial >= grupos.size() || usadas.size() > objetivo)
            return null;

        for (int i = indiceInicial; i < grupos.size(); i++) {
            // 2. Ahora el grupo es de tipo TipoJuego (Trío o Escalera)
            TipoJuego grupo = grupos.get(i);

            // 3. Verificar Solapamiento: Usamos grupo.getCartas() para acceder a la lista
            boolean haySolapamiento = grupo.getCartas().stream().anyMatch(usadas::contains);
            if (haySolapamiento)
                continue;

            if (usadas.size() + grupo.getCartas().size() > objetivo)
                continue;

            List<Carta> combinadas = new ArrayList<>(usadas);
            // 4. Añadimos las cartas reales usando grupo.getCartas()
            combinadas.addAll(grupo.getCartas());

            List<Carta> resultado = buscarCombinacion(grupos, objetivo, combinadas, i + 1);
            if (resultado != null)
                return resultado;
        }
        return null;
    }

    public  List<Carta> cartasNoUsadas(List<Carta> mano) {
        List<TipoJuego> combinacionesPosibles = new ArrayList<>();
        combinacionesPosibles.addAll(this.encontrarTrio(mano));
        combinacionesPosibles.addAll(this.encontrarEscaleras(mano));

        List<Carta> cartasUsadas = null;
        // Buscamos la combinación más larga >= 3 cartas
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

        // Si no encontramos ningún trio ni escalera de ≥3 cartas
        return new ArrayList<>(mano);
    }

    public boolean esListaCerrable (List<Carta> mano){
        //solo para el testeo
        if (mano.size() !=7)
            return false;
        List<TipoJuego> combinacionesPosibles = new ArrayList<>();
        // Dentro de esListaCerrable:
        combinacionesPosibles.addAll(this.encontrarTrio(mano)); // Pasa la variable 'mano' como argumento
        combinacionesPosibles.addAll(this.encontrarEscaleras(mano));
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


    private List<Escalera> encontrarEscaleras(List<Carta> mano) {
        Map<Palo, List<Carta>> porPalo = new HashMap<>();
        for (Carta carta : mano) { // USAR 'mano'
            porPalo.computeIfAbsent(carta.getPalo(), k -> new ArrayList<>()).add(carta);
        }

        // Cambiamos el tipo de retorno: devolverá objetos Escalera
        List<Escalera> escalasValidas = new ArrayList<>();

        // (Resto de la lógica del compañero para encontrar secuencias por palo...)
        for (List<Carta> grupo : porPalo.values()) {
            Set<Integer> numerosUnicos = new TreeSet<>();
            for (Carta c : grupo)
                numerosUnicos.add(c.getNumero());
            List<Integer> listaNumeros = new ArrayList<>(numerosUnicos);

            for (int i = 0; i < listaNumeros.size(); i++) {
                List<Carta> secuencia = new ArrayList<>();
                for (int j = i; j < listaNumeros.size(); j++) {
                    // (Lógica de secuencia...)
                    if (j > i && listaNumeros.get(j) != listaNumeros.get(j - 1) + 1)
                        break;
                    int num = listaNumeros.get(j);
                    Optional<Carta> opcion = grupo.stream()
                            .filter(c -> c.getNumero() == num && !secuencia.contains(c))
                            .findFirst();
                    if (!opcion.isPresent())
                        break;
                    secuencia.add(opcion.get());

                    // Si encontramos una secuencia válida (>= 3), CREAMOS un objeto Escalera
                    if (secuencia.size() >= 3) {
                        // Crea y guarda el objeto Escalera, no una lista de listas
                        escalasValidas.add(new Escalera(new ArrayList<>(secuencia)));
                    }
                }
            }
        }
        return escalasValidas;
    }

    private List<Trio> encontrarTrio(List<Carta> mano) {
        Map<Integer, List<Carta>> porNumero = new HashMap<>();
        for (Carta carta : mano) { // USAR 'mano'
            porNumero.computeIfAbsent(carta.getNumero(), k -> new ArrayList<>()).add(carta);
        }

        // Cambiamos el tipo de retorno: devolverá objetos Trio
        List<Trio> triosValidos = new ArrayList<>();
        for (Map.Entry<Integer, List<Carta>> entrada : porNumero.entrySet()) {
            List<Carta> grupo = entrada.getValue();
            if (grupo.size() >= 3) {

                // 1. Usar combinar para obtener todas las posibles combinaciones de 3
                List<List<Carta>> combinacionesDeTres = this.combinar(grupo, 3);
                for (List<Carta> listaCartas : combinacionesDeTres) {
                    triosValidos.add(new Trio(listaCartas)); // Crea y guarda el objeto Trio
                }

                // 2. Si hay 4 cartas, también es una combinación válida (el cuarteto)
                if (grupo.size() == 4) {
                    triosValidos.add(new Trio(new ArrayList<>(grupo))); // Crea el objeto Trio (cuarteto)
                }
            }
        }
        return triosValidos;
    }

}
