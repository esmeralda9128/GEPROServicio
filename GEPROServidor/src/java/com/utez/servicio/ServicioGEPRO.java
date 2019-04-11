/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utez.servicio;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Application;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utez.edu.modelo.bean.BeanActividad;
import utez.edu.modelo.bean.BeanNomina;
import utez.edu.modelo.bean.BeanProyecto;
import utez.edu.modelo.bean.BeanRecursoComprado;
import utez.edu.modelo.bean.BeanRecursoMaterial;
import utez.edu.modelo.bean.BeanUsuario;
import utez.edu.modelo.dao.DaoActividades;
import utez.edu.modelo.dao.DaoNomina;
import utez.edu.modelo.dao.DaoProyecto;
import utez.edu.modelo.dao.DaoRecursoMaterial;
import utez.edu.modelo.dao.DaoRecursoMaterialComprado;
import utez.edu.modelo.dao.DaoUsuario;

/**
 *
 * @author Esmeralda Estefanía Rodríguez Ramos
 * @version 15/03/2019
 */
@ApplicationPath("/servicioGEPRO")
@Path("/proyecto")
public class ServicioGEPRO extends Application {

    String mensaje = "";
    String tipo = "";
    Map respuestas = new HashMap();
    public static int idProyectoGlobal;
    public static double totalGlobal;
    public static String fechaGlobal;
    public static int semanaGlobal;
    public static int usarioPagarGlobal;
    public static List<BeanRecursoMaterial> materialesGlobal = new ArrayList<>();
    public static int idUsuarioGlobal;

    @GET
    @Path("registroProyecto")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registrarProyecto(@QueryParam("proyecto") String proyecto, @QueryParam("usuario") String usuario) throws ParseException {
        DaoProyecto daoProyecto = new DaoProyecto();
        JSONObject proyectoJ = null;
        JSONObject usuarioJ = null;
        BeanProyecto beanProyecto = null;
        BeanUsuario beanUsuario = null;
        String conpass = "";
        boolean bandera = false;
        boolean registro = false;
        try {
            proyectoJ = new JSONObject(proyecto);
            usuarioJ = new JSONObject(usuario);
            beanProyecto = new BeanProyecto(proyectoJ.getString("nombre"), proyectoJ.getString("fecha"), Integer.parseInt(proyectoJ.getString("semanas")), Double.parseDouble(proyectoJ.getString("presupuesto")), Double.parseDouble(proyectoJ.getString("reserva")));
            beanUsuario = new BeanUsuario(usuarioJ.getString("nombre"), usuarioJ.getString("apellidoP"), usuarioJ.getString("apellidoM"), usuarioJ.getString("usuario"), usuarioJ.getString("pass"), usuarioJ.getString("grado"), usuarioJ.getString("carrera"), usuarioJ.getString("rfc"), usuarioJ.getString("email"), Double.parseDouble(usuarioJ.getString("salario")));
            conpass = usuarioJ.getString("conpass");
            if (usuarioJ.getString("rfc").length() == 0 || usuarioJ.getString("email").length() == 0 || (usuarioJ.getString("salario").length() == 0)) {
                mensaje = "No puedes enviar campos vacios";
                tipo = "error";
            } else {
                bandera = true;
            }
        } catch (JSONException ex) {
            System.out.println("Error" + ex);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String inico = beanProyecto.getInicioProyecto();
        java.util.Date fechaInicio = sdf.parse(inico);
        Date fechaFin = sumarDiasAFecha(fechaInicio, ((beanProyecto.getSemanas() * 7) - 2));
        String fechaFinString = sdf.format(fechaFin);
        beanProyecto.setFinalProyecto(fechaFinString);

        Calendar fecha = new GregorianCalendar();

        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        String actual = "" + año + "-" + (mes + 1) + "-" + dia;
        java.util.Date fechadateactual = sdf.parse(actual);
        if (bandera) {
            if (conpass.equals(beanUsuario.getPass())) {
//                if (fechadateactual.before(fechaInicio)) {
                if (beanProyecto.getPresupuestoInicial() > beanUsuario.getSalario()) {
                    if (daoProyecto.verificarNombredeLider(beanUsuario) == null) {
                        if (daoProyecto.verificarNombredeProyecto(beanProyecto) == null) {
                            registro = daoProyecto.registrarProyecto(beanProyecto, beanUsuario);
                            if (registro) {
                                mensaje = "Se ha registrado el Proyecto Correctamente";
                                tipo = "success";
                            } else {
                                mensaje = "No se ha podido registrar el proyecto";
                                tipo = "error";
                            }
                        } else {
                            mensaje = "Ya existe un Proyecto con ese nombre";
                            tipo = "error";
                        }
                    } else {
                        mensaje = "Ese usuario ya esta asignado a otro Proyecto";
                        tipo = "error";
                    }
                } else {
                    mensaje = "El salario del Líder de Proyecto no puede ser mayor al Presupuesto";
                    tipo = "error";
                }

//                } else {
//                    mensaje = "No puedes iniciar un proyecto antes de la fecha actual";
//                    tipo = "error";
//                }
            } else {
                mensaje = "Las contraseñas no coinciden";
                tipo = "error";
            }
        }

        List<BeanProyecto> proyectos = daoProyecto.consultarProyectos();
        respuestas.put("proyectos", proyectos);
        respuestas.put("mensaje", mensaje);
        respuestas.put("tipo", tipo);
        respuestas.put("registro", registro);
        try {

            proyectoJ.put("respuesta", respuestas);

        } catch (JSONException e) {
            System.out.println("Error" + e);
        }
        Response.ResponseBuilder constructor = Response.ok(proyectoJ.toString());

        constructor.header(
                "Access-Control-Allow-Origin", "*");
        constructor.header(
                "Access-Control-Allow-Methods", "*");
        return constructor.build();
    }

    @GET
    @Path("consultarProyectos")
    @Produces(MediaType.APPLICATION_JSON)
    public Response consultarProyectos() {
        DaoProyecto daoProyecto = new DaoProyecto();
        List<BeanProyecto> proyectos = daoProyecto.consultarProyectos();
        respuestas.put("proyectos", proyectos);
        JSONObject objeto = new JSONObject();
        try {

            objeto.put("respuesta", respuestas);

        } catch (JSONException e) {
            System.out.println("Error" + e);
        }
        Response.ResponseBuilder constructor = Response.ok(objeto.toString());
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();

    }

    @GET
    @Path("eliminarProyecto")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response elimiarProyecto(@QueryParam("proyecto") String proyecto) {
        DaoProyecto daoProyecto = new DaoProyecto();
        int idProyecto = 0;
        JSONObject proyectoJ = null;
        try {
            proyectoJ = new JSONObject(proyecto);
            idProyecto = proyectoJ.getInt("idProyecto");
        } catch (JSONException ex) {
            System.out.println("Error" + ex);
        }
        if (daoProyecto.eliminarProyecto(idProyecto)) {
            mensaje = "Se ha eliminado el proyecto";
            tipo = "success";
        } else {
            mensaje = "Ocurrio un error";
            tipo = "error";
        }
        List<BeanProyecto> proyectos = daoProyecto.consultarProyectos();
        respuestas.put("proyectos", proyectos);
        respuestas.put("mensaje", mensaje);
        respuestas.put("tipo", tipo);
        try {

            proyectoJ.put("respuesta", respuestas);

        } catch (JSONException e) {
            System.out.println("Error" + e);
        }
        Response.ResponseBuilder constructor = Response.ok(proyectoJ.toString());
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }

    @GET
    @Path("consultarProyecto")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response consultarProyecto(@QueryParam("proyecto") String proyecto) {
        DaoProyecto daoProyecto = new DaoProyecto();

        JSONObject proyectoJ = null;
        try {
            proyectoJ = new JSONObject(proyecto);
            idProyectoGlobal = proyectoJ.getInt("idProyecto");
        } catch (JSONException ex) {
            System.out.println("Error" + ex);
        }

        Response.ResponseBuilder constructor = Response.ok(proyectoJ.toString());
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }

    @GET
    @Path("consultarPerfilAdmin")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response consultarPerfilAdmin() {
        DaoUsuario daoUsuario = new DaoUsuario();
        BeanUsuario beanUsuario = daoUsuario.consultarPerfilAdministrador();
        JSONObject objeto = new JSONObject();
        respuestas.put("usuario", beanUsuario);
        try {

            objeto.put("respuesta", respuestas);

        } catch (JSONException e) {
            System.out.println("Error" + e);
        }

        Response.ResponseBuilder constructor = Response.ok(objeto.toString());
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }

    @GET
    @Path("seguimientoAdmin")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response seguimientoAdmin() throws ParseException {
        DaoProyecto daoProyecto = new DaoProyecto();
        DaoUsuario daoUsuario = new DaoUsuario();
        DaoRecursoMaterial daoMaterial = new DaoRecursoMaterial();
        JSONObject proyectoJ = new JSONObject();

        BeanProyecto proyectoConsultado = daoProyecto.consultarProyectoporId(idProyectoGlobal);
        String semana;
        Double valorPlaneado = 0.0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fin = proyectoConsultado.getFinalProyecto();
        java.util.Date fechaFin = sdf.parse(fin);
        Calendar fecha = new GregorianCalendar();
        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        String actual = "" + año + "-" + (mes + 1) + "-" + dia;
        java.util.Date fechadateactual = sdf.parse(actual);

        if (fechadateactual.before(fechaFin)) {
            semana = Integer.toString(((daoProyecto.consultarDias(proyectoConsultado.getInicioProyecto())) / 7) + 1);
            valorPlaneado = (proyectoConsultado.getPresupuestoInicial() / proyectoConsultado.getSemanas()) * Integer.parseInt(semana);
        } else {
            semana = "El proyecto ya termino";
        }

        respuestas.put("proyecto", proyectoConsultado);
        respuestas.put("lider", daoUsuario.consultarLiderdeProyecto(idProyectoGlobal));
        respuestas.put("recursosHumanos", daoUsuario.consultarRescursos(idProyectoGlobal));
        respuestas.put("recursosMateriales", daoMaterial.listaRecursos(idProyectoGlobal));
        respuestas.put("presuPuestoGastado", daoProyecto.consultarPresupuestoGastado(idProyectoGlobal));
        respuestas.put("semana", semana);
        respuestas.put("valorPlaneado", valorPlaneado);

        try {

            proyectoJ.put("respuesta", respuestas);

        } catch (JSONException e) {
            System.out.println("Error" + e);
        }
        Response.ResponseBuilder constructor = Response.ok(proyectoJ.toString());
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }

    @GET
    @Path("seguimientoProyecto")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response seguimiento(@QueryParam("proyecto") String proyecto) throws ParseException {
        DaoProyecto daoProyecto = new DaoProyecto();
        DaoUsuario daoUsuario = new DaoUsuario();
        DaoRecursoMaterial daoMaterial = new DaoRecursoMaterial();
        DaoRecursoMaterialComprado daoRecursoMaterialComprado = new DaoRecursoMaterialComprado();
        JSONObject proyectoJ = null;
        int idProyecto = 0;
        try {
            proyectoJ = new JSONObject(proyecto);
            idProyecto = proyectoJ.getInt("proyecto");
        } catch (JSONException ex) {
            System.out.println("Error" + ex);
        }

        BeanProyecto proyectoConsultado = daoProyecto.consultarProyectoporId(idProyecto);
        int semana = 0;
        Double valorPlaneado = 0.0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fin = proyectoConsultado.getFinalProyecto();
        java.util.Date fechaFin = sdf.parse(fin);
        Calendar fecha = new GregorianCalendar();
        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        String actual = "" + año + "-" + (mes + 1) + "-" + dia;
        java.util.Date fechadateactual = sdf.parse(actual);

        if (fechadateactual.before(fechaFin)) {
            semana = ((daoProyecto.consultarDias(proyectoConsultado.getInicioProyecto())) / 7) + 1;
            valorPlaneado = (proyectoConsultado.getPresupuestoInicial() / proyectoConsultado.getSemanas()) * semana;
        } else {
            semana = 0;
        }

        List<BeanRecursoComprado> materialesComprados = daoRecursoMaterialComprado.buscarRecursosComprado(idProyecto, semana);
        List<BeanUsuario> recursosHumanos = daoUsuario.consultarRescursos(idProyecto);
        List<BeanRecursoMaterial> materialesProyecto = daoMaterial.listaRecursos(idProyecto);
        List<BeanRecursoMaterial> materialPorComprar = new ArrayList<>();
        double gastado = daoProyecto.consultarPresupuestoGastado(idProyecto);
        respuestas.put("fecha", fechadateactual);
        respuestas.put("proyecto", proyectoConsultado);
        respuestas.put("lider", daoUsuario.consultarLiderdeProyecto(idProyecto));
        respuestas.put("recursosHumanos", daoUsuario.consultarRescursos(idProyecto));
        respuestas.put("recursosMateriales", daoMaterial.listaRecursos(idProyecto));
        respuestas.put("semana", semana);
        respuestas.put("presuPuestoGastado", daoProyecto.consultarPresupuestoGastado(idProyecto));
        respuestas.put("valorPlaneado", valorPlaneado);
        respuestas.put("fecha", actual);
        respuestas.put("gastado", gastado);

        try {

            proyectoJ.put("respuesta", respuestas);

        } catch (JSONException e) {
            System.out.println("Error" + e);
        }
        Response.ResponseBuilder constructor = Response.ok(proyectoJ.toString());
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }

    @GET
    @Path("registroRecursoHumano")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registrarRecursoHumano(@QueryParam("usuario") String usuario) throws ParseException {
        DaoUsuario daoUsuario = new DaoUsuario();
        BeanUsuario beanUsuario = null;
        JSONObject usuarioJ = null;
        String conpass = "";
        boolean registro = false;
        int idProyecto = 0;
        try {
            usuarioJ = new JSONObject(usuario);
            beanUsuario = new BeanUsuario(usuarioJ.getString("nombre"), usuarioJ.getString("apellidoP"), usuarioJ.getString("apellidoM"), usuarioJ.getString("usuario"), usuarioJ.getString("pass"), usuarioJ.getString("grado"), usuarioJ.getString("carrera"), usuarioJ.getString("rfc"), usuarioJ.getString("email"), Double.parseDouble(usuarioJ.getString("salario")));
            beanUsuario.setRol(usuarioJ.getString("rol"));
            conpass = usuarioJ.getString("conpass");
            idProyecto = usuarioJ.getInt("idProyecto");

        } catch (JSONException e) {
            System.out.println("Error" + e);
        }

        if (conpass.equals(beanUsuario.getPass())) {
            if (daoUsuario.consultarUsuariosRepetidos(beanUsuario) == null) {
                registro = daoUsuario.registrarRecursoHumano(beanUsuario, idProyecto);
                if (registro) {
                    mensaje = "Se ha registrado correctamente el recurso";
                    tipo = "success";
                } else {
                    mensaje = "No se pudo registrar el recurso";
                    tipo = "error";
                }
            } else {
                mensaje = "Ese recurso ya esta registrado";
                tipo = "error";
            }
        } else {
            mensaje = "Las contraseñas no coinciden";
            tipo = "error";
        }

        respuestas.put("mensaje", mensaje);
        respuestas.put("tipo", tipo);
        respuestas.put("registro", registro);
        try {

            usuarioJ.put("respuesta", respuestas);

        } catch (JSONException e) {
            System.out.println("Error" + e);
        }
        Response.ResponseBuilder constructor = Response.ok(usuarioJ.toString());
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }

    @GET
    @Path("registroRecursoMaterial")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registrarRecursoMaterial(@QueryParam("material") String material) {
        DaoRecursoMaterial daoRecursoMaterial = new DaoRecursoMaterial();
        BeanRecursoMaterial beanRecursoMaterial = null;
        JSONObject objeto = null;
        String conpass = ""; // Esta wea no se ocupa
        boolean registro = false;
        try {
            objeto = new JSONObject(material);
            beanRecursoMaterial = new BeanRecursoMaterial(objeto.getString("nombre"), objeto.getDouble("precio"), objeto.getInt("cantidad"));
            idProyectoGlobal = objeto.getInt("idProyecto");
            Double total = beanRecursoMaterial.getCostoUnitario() * beanRecursoMaterial.getCantidad();
            beanRecursoMaterial.setTotal(total);
        } catch (JSONException e) {
            System.out.println("Error" + e);
        }

        if (daoRecursoMaterial.consultarRecursoRepetido(beanRecursoMaterial, idProyectoGlobal) == null) {
            registro = daoRecursoMaterial.registrarRecursoMaterial(beanRecursoMaterial, idProyectoGlobal);
            if (registro) {
                mensaje = "Se ha registrado correctamente el recurso";
                tipo = "success";
            } else {
                mensaje = "No se pudo registrar el recurso";
                tipo = "error";
            }
        } else {
            mensaje = "Ese recurso ya esta registrado en el proyecto";
            tipo = "error";
        }

        respuestas.put("mensaje", mensaje);
        respuestas.put("tipo", tipo);
        respuestas.put("registro", registro);
        respuestas.put("recursosMateriales", daoRecursoMaterial.listaRecursos(idProyectoGlobal));
        try {
            objeto.put("respuesta", respuestas);

        } catch (JSONException e) {
            System.out.println("Error" + e);
        }
        Response.ResponseBuilder constructor = Response.ok(objeto.toString());
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }

    @GET
    @Path("consultarPerfilLider")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response consultarPerfilLider(@QueryParam("idUsuario") String idUsuario) {
        DaoUsuario daoUsuario = new DaoUsuario();
        int idUsuarioRegresado = 0;
        JSONObject objeto = null;
        try {
            objeto = new JSONObject(idUsuario);
            idUsuarioRegresado = objeto.getInt("proyecto");
        } catch (JSONException e) {
            System.out.println(e);
        }
        BeanUsuario beanUsuario = daoUsuario.consultarLiderdeProyecto(idUsuarioRegresado);
        respuestas.put("usuario", beanUsuario);
        try {

            objeto.put("respuesta", respuestas);

        } catch (JSONException e) {
            System.out.println("Error" + e);
        }

        Response.ResponseBuilder constructor = Response.ok(objeto.toString());
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }

    @GET
    @Path("mostrarAlertasRecursos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response comprarRecurso(@QueryParam("materiales") String materiales) throws ParseException {
        JSONObject objeto = new JSONObject();
        JSONArray array = null;
        DaoRecursoMaterial daoRecursoMaterial = new DaoRecursoMaterial();
        DaoProyecto daoProyecto = new DaoProyecto();
        DaoRecursoMaterialComprado daoRecursoComprado = new DaoRecursoMaterialComprado();
        List<BeanRecursoMaterial> materialesporComprar = new ArrayList<>();
        double total = 0;
        int semana = 0;
        String mensaje2 = "";
        boolean bandera = false;
        String actual = "";

        try {
            array = new JSONArray(materiales);
            for (int i = 0; i < array.length(); i++) {
                BeanRecursoMaterial recurso = daoRecursoMaterial.buscarRecursoComprado(array.getInt(i));
                materialesporComprar.add(recurso);
                total += recurso.getTotal();
            }
        } catch (JSONException ex) {
            System.out.println("Error" + ex);
        }
        if (array.length() != 0) {

            BeanProyecto proyectoConsultado = daoProyecto.consultarProyectoporId(materialesporComprar.get(0).getIdProyecto());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fin = proyectoConsultado.getFinalProyecto();
            java.util.Date fechaFin = sdf.parse(fin);
            Calendar fecha = new GregorianCalendar();
            int año = fecha.get(Calendar.YEAR);
            int mes = fecha.get(Calendar.MONTH);
            int dia = fecha.get(Calendar.DAY_OF_MONTH);
            actual = "" + año + "-" + (mes + 1) + "-" + dia;
            java.util.Date fechadateactual = sdf.parse(actual);

            if (fechadateactual.before(fechaFin)) {
                semana = ((daoProyecto.consultarDias(proyectoConsultado.getInicioProyecto())) / 7) + 1;
            } else {
                semana = 0;
            }
            for (int i = 0; i < materialesporComprar.size(); i++) {
                if (daoRecursoComprado.buscarRecursoComprado(materialesporComprar.get(i).getIdRecuroMat(), semana) != null) {

                    mensaje = "Ya compraste alguno de esos materiales esta semana";
                    mensaje2 = "No puedes repetir recursos en la misma semana";
                    tipo = "error";
                    idProyectoGlobal = materialesporComprar.get(0).getIdProyecto();
                    bandera = true;
                }
            }
            if (!bandera) {
                mensaje = "¿Seguro que quieres comprar los Recursos Materiales?";
                mensaje2 = "El total es " + total;
                tipo = "question";
            }
        }
        System.out.println("Tamaño de arreglo original" + materialesporComprar.size());
        totalGlobal = total;

        materialesGlobal = materialesporComprar;
        fechaGlobal = actual;
        semanaGlobal = semana;
        respuestas.put("mensaje", mensaje);
        respuestas.put("mensaje2", mensaje2);
        respuestas.put("materialesporComprar", materialesporComprar);
        respuestas.put("total", total);
        respuestas.put("bandera", bandera);
        try {
            objeto.put("respuesta", respuestas);

        } catch (JSONException e) {
            System.out.println("Error" + e);
        }
        Response.ResponseBuilder constructor = Response.ok(objeto.toString());
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }

    @GET
    @Path("comprarRecursosMateriales")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response comprarRecursos() {
        JSONObject objeto = new JSONObject();

        DaoProyecto daoProyecto = new DaoProyecto();
        DaoRecursoMaterialComprado daoComprado = new DaoRecursoMaterialComprado();
        System.out.println("total" + totalGlobal);
        System.out.println("materiales tamaño" + materialesGlobal.size());
        System.out.println("id de proyecto" + idProyectoGlobal);
        BeanProyecto proyecto = daoProyecto.consultarProyectoporId(idProyectoGlobal);
        if (proyecto.getPresupustoActual() > totalGlobal) {
            for (int i = 0; i < materialesGlobal.size(); i++) {
                if (daoComprado.comprarRecursoMaterial(idProyectoGlobal, materialesGlobal.get(i).getIdRecuroMat(), fechaGlobal, semanaGlobal)) {
                    mensaje = "Se han comprado los materiales correctamente";
                    tipo = "success";
                } else {
                    mensaje = "No se compraron los materiales apartir del material " + materialesGlobal.get(i).getNombreRecursoMat();
                    tipo = "error";
                    break;
                }
            }
        }
        respuestas.put("mensaje", mensaje);
        respuestas.put("tipo", tipo);
        try {
            objeto.put("respuesta", respuestas);

        } catch (JSONException e) {
            System.out.println("Error" + e);
        }
        Response.ResponseBuilder constructor = Response.ok(objeto.toString());
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }

    @GET
    @Path("actualizarPerfilAdmin")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response actualizarPerfilAdmin(@QueryParam("perfilAdmin") String perfilAdmin) throws ParseException {
        DaoUsuario daoUsuario = new DaoUsuario();
        BeanUsuario beanAdmin = new BeanUsuario();
        JSONObject usuarioJ = null;
        String conpass = "";
        boolean registro = false;
        try {
            usuarioJ = new JSONObject(perfilAdmin);
            beanAdmin.setNombre(usuarioJ.getString("nombre"));
            beanAdmin.setUsuario(usuarioJ.getString("usuario"));
            // La contraseña pasada se agrega - en caso de no existir modificaciones
            beanAdmin.setPass(usuarioJ.getString("pass"));
            conpass = usuarioJ.getString("pass");

            beanAdmin.setCarrera(usuarioJ.getString("carrera"));
            beanAdmin.setGradoEstudios(usuarioJ.getString("gradoEstudios"));

            // Verificar si existe algo en la nueva contraseña
            if (usuarioJ.getString("newPass").length() != 0) {
                // Si existe algo se agrega al beanUsuario - Con la finalidad de reemplazar
                beanAdmin.setPass(usuarioJ.getString("newPass"));
                conpass = usuarioJ.getString("confirmNewPass");
            }
        } catch (JSONException e) {
            System.out.println("Error" + e);
        }

        // Verificaciones
        if (conpass.equals(beanAdmin.getPass())) {
            registro = daoUsuario.modificarPerfilAdministrador(beanAdmin);
            if (registro) {
                mensaje = "Se ha actualizado correctamente el perfil de administrador";
                tipo = "success";
            } else {
                mensaje = "No se pudo actualizar el perfil de administrador";
                tipo = "error";
            }
        } else {
            mensaje = "Las contraseñas no coinciden";
            tipo = "error";
        }

        respuestas.put("mensaje", mensaje);
        respuestas.put("tipo", tipo);
        respuestas.put("registro", registro);
        try {
            usuarioJ.put("respuesta", respuestas);
        } catch (JSONException e) {
            System.out.println("Error" + e);
        }
        Response.ResponseBuilder constructor = Response.ok(usuarioJ.toString());
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }

    @GET
    @Path("usuarioPagar")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response usuarioPagar(@QueryParam("usuario") String usuario) {
        JSONObject objeto = null;
        int idUsuario = 0;
        int idProyecto = 0;
        try {
            objeto = new JSONObject(usuario);
            idUsuario = objeto.getInt("idUsurio");
            idProyecto = objeto.getInt("idProyecto");
        } catch (JSONException ex) {
            System.out.println("Error" + ex);
        }
        idProyectoGlobal = idProyecto;
        usarioPagarGlobal = idUsuario;
        if (objeto == null) {
            objeto = new JSONObject();
        }
        Response.ResponseBuilder constructor = Response.ok(objeto.toString());
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }

    @GET
    @Path("pagarNomina")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response pagarNomina(@QueryParam("valorGanado") String valorGanado) throws ParseException {
        DaoProyecto daoProyecto = new DaoProyecto();
        DaoUsuario daoUsuario = new DaoUsuario();
        DaoNomina daoNomina = new DaoNomina();

        JSONObject objeto = null;
        double valorGanadoRecibido = 0;
        int semana = 0;
        boolean bandera = false;
        String actual = "";
        try {
            objeto = new JSONObject(valorGanado);

            valorGanadoRecibido = objeto.getDouble("valorGanado");
            if ((objeto.getString("valorGanado")).length() != 0) {
                bandera = true;
            }
        } catch (JSONException e) {
            System.out.println(e);
        }

        BeanProyecto proyectoConsultado = daoProyecto.consultarProyectoporId(idProyectoGlobal);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fin = proyectoConsultado.getFinalProyecto();
        java.util.Date fechaFin = sdf.parse(fin);
        Calendar fecha = new GregorianCalendar();
        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        actual = "" + año + "-" + (mes + 1) + "-" + dia;
        java.util.Date fechadateactual = sdf.parse(actual);

        if (fechadateactual.before(fechaFin)) {
            semana = ((daoProyecto.consultarDias(proyectoConsultado.getInicioProyecto())) / 7) + 1;
        } else {
            semana = 0;
        }
        if (bandera) {
            if ((daoNomina.consultarNomina(semana, usarioPagarGlobal)) == null) {
                if (daoNomina.pagarNomina(usarioPagarGlobal, idProyectoGlobal, actual, semana, valorGanadoRecibido)) {
                    int num = daoProyecto.contarRecursosHumanos(idProyectoGlobal);
                    double valorGanadoSuma = daoNomina.sumaValorGanado(idProyectoGlobal, semana);
                    double valorGanadoPorcentage = (valorGanadoSuma / num);
                    double valorGanadoAsignar = ((proyectoConsultado.getPresupuestoInicial() * 26) / 100);
                    if (daoProyecto.agregarValorGanado(idProyectoGlobal, valorGanadoAsignar)) {
                        mensaje = "Se ha pagado la nómina correctamente";
                        tipo = "success";
                    } else {
                        mensaje = "Ocurrio un error al asignar el valor ganado";
                        tipo = "error";
                    }

                } else {
                    mensaje = "No se pudo pagar la nómina";
                    tipo = "error";
                }
            } else {
                mensaje = "Ya se pago esta nómina esta semana";
                tipo = "warning";
            }
        } else {
            mensaje = "Debes ingresar un valor ganado";
            tipo = "warning";
        }
        respuestas.put("mensaje", mensaje);
        respuestas.put("tipo", tipo);
        try {
            objeto.put("respuesta", respuestas);

        } catch (JSONException e) {
            System.out.println("Error" + e);
        }
        Response.ResponseBuilder constructor = Response.ok(objeto.toString());
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }

    public static Date sumarDiasAFecha(Date fecha, int dias) {
        if (dias == 0) {
            return fecha;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(Calendar.DAY_OF_YEAR, dias);
        return calendar.getTime();
    }

    @GET
    @Path("seguimientoEmpleados")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response seguimientoEmpleado(@QueryParam("idProyecto") String idProyecto1) {
        JSONObject objetoJSON = null;
        int idProyecto = 0;
        DaoUsuario miDaoUsuario = new DaoUsuario();
        List<BeanUsuario> usuarios = null;
        try {
            objetoJSON = new JSONObject(idProyecto1);
            idProyecto = objetoJSON.getInt("proyecto");
        } catch (JSONException ex) {
            System.out.println("Error" + ex);
        }
        usuarios = miDaoUsuario.consultarUsuarios3(idProyecto);
        System.out.println(usuarios.size());
        try {
            objetoJSON = new JSONObject();
            if (usuarios == null) {
                objetoJSON.put("mensaje", "No hay usuarios registrados...");
                objetoJSON.put("tipo", "error");
            } else {
                objetoJSON.put("mensaje", "Usuarios encontrados");
                objetoJSON.put("tipo", "success");
                respuestas.put("usuarios", usuarios);
                objetoJSON.put("respuesta", respuestas);
            }
        } catch (JSONException ex) {
            System.out.println(ex);
        }
        Response.ResponseBuilder constructor = Response.ok(objetoJSON.toString());
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }

    @GET
    @Path("mostrarActividades")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response mostrarActividades() {
        JSONObject objetoJSON = null;
        DaoUsuario miDaoUsuario = new DaoUsuario();
        List<BeanActividad> actividades = null;
        try {
            System.out.println("El idGobal es " + idUsuarioGlobal);
            actividades = miDaoUsuario.mostrarActividades(idUsuarioGlobal);
            objetoJSON = new JSONObject();
            if (actividades.isEmpty()) {
                objetoJSON.put("mensaje", "No hay usuarios registrados...");
                objetoJSON.put("tipo", "error");
            } else {
                objetoJSON.put("mensaje", "Usuarios encontrados");
                objetoJSON.put("tipo", "success");
                respuestas.put("actividades", actividades);
                objetoJSON.put("respuesta", respuestas);
            }
        } catch (JSONException ex) {
            System.out.println("Error" + ex);
        }
        Response.ResponseBuilder constructor = Response.ok(objetoJSON.toString());
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }

    @GET
    @Path("consultarActividadId")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response consultarActividadId(@QueryParam("id") String id) {
        JSONObject proyectoJ = null;
        try {
            proyectoJ = new JSONObject(id);
            idUsuarioGlobal = proyectoJ.getInt("id");
            System.out.println("EL id en consultar actividad id es " + idUsuarioGlobal);
        } catch (JSONException ex) {
            System.out.println("Error" + ex);
        }
        Response.ResponseBuilder constructor = Response.ok(proyectoJ.toString());
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }
    
    /*Aqui vienen todos los métodos utilizados para la aplicacion móvil*/
    @POST
    @Path("consultaUsuarios")
    @Produces(MediaType.APPLICATION_JSON)
    public Response consultaUsuarios(BeanUsuario user) {
        System.out.println("Entro al método del login");
        BeanUsuario usuario = null;
        DaoUsuario daoUsuario = null;
        JSONObject objeto = null;
        try {
            daoUsuario = new DaoUsuario();
            usuario = new BeanUsuario();
            usuario = daoUsuario.cosultaLogin(user.getUsuario(), user.getPass());
            System.out.println("Usuario->" + usuario);
            objeto = new JSONObject();
            objeto.put("usuarios", usuario);
            System.out.println(objeto);
        } catch (Exception e) {
            System.out.println("Error en el método login");
        }
        Response.ResponseBuilder constructor = Response.ok(usuario);
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }

    @POST
    @Path("consultaProyecto")
    @Produces(MediaType.APPLICATION_JSON)

    public Response consultaProyecto(BeanUsuario user) {
        System.out.println("Entro al método de consultar proyecto");
        BeanProyecto proyecto = null;
        DaoProyecto dao = null;
        JSONObject objeto = null;
        try {
            dao = new DaoProyecto();
            proyecto = new BeanProyecto();
            proyecto = dao.consultarProyectoporId(user.getIdProyecto());
            System.out.println("Proyecto->" + proyecto);

            System.out.println(proyecto);
        } catch (Exception e) {
            System.out.println("Error en el método login");
        }
        Response.ResponseBuilder constructor = Response.ok(proyecto);
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }
    @POST
    @Path("registroActividad")
    public Response registroActividad(BeanActividad actividad) {
        System.out.println("Entro al método del registro de actividad");
        BeanUsuario usuario = null;
        DaoActividades dao = null;
        JSONObject objeto = null;
        Integer registro=0;
        try {
            dao=new DaoActividades();
            
            registro=dao.registroActividad(actividad);
          
        } catch (Exception e) {
            System.out.println("Error en el método registrar actividad");
        }
        Response.ResponseBuilder constructor = Response.ok(registro);
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }
    @POST
    @Path("consultaNominas")
    @Produces(MediaType.APPLICATION_JSON)
    public Response consultaNominas(BeanUsuario usuario) {
        System.out.println("Entro al método de consultas de nomina");
        List<BeanNomina>nominas= new ArrayList<>();
        JSONObject  objeto = null;
        DaoNomina daoNomina = null;
        try {
            daoNomina= new DaoNomina();
            nominas=daoNomina.consultaNomina(usuario);
            objeto= new JSONObject();
            objeto.put("nominas",nominas);
            for (int i = 0; i < nominas.size(); i++) {
                System.out.println(nominas.get(i).getIdUsuario());
            }
        } catch (Exception e) {
            System.out.println("Error en el método registrar actividad");
        }
        Response.ResponseBuilder constructor = Response.ok(objeto.toString());
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }


    @GET
    @Path("variacionCronogramaLider")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response variacionCronogramaLider(@QueryParam("idProyecto") String idProyecto) throws ParseException {
        JSONObject proyectoJ = null;
        DaoProyecto daoProyecto = new DaoProyecto();
        BeanProyecto proyectoConsultado = null;
        String mensaje2 = "";
        String actual = "";
        int semana = 0;
        int idProyectoConsultado = 0;
        try {
            proyectoJ = new JSONObject(idProyecto);
            idProyectoConsultado = proyectoJ.getInt("proyecto");

        } catch (JSONException ex) {
            System.out.println("Error" + ex);
        }

        proyectoConsultado = daoProyecto.consultarProyectoporId(idProyectoConsultado);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fin = proyectoConsultado.getFinalProyecto();
        java.util.Date fechaFin = sdf.parse(fin);
        Calendar fecha = new GregorianCalendar();
        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        actual = "" + año + "-" + (mes + 1) + "-" + dia;
        java.util.Date fechadateactual = sdf.parse(actual);

        if (fechadateactual.before(fechaFin)) {
            semana = ((daoProyecto.consultarDias(proyectoConsultado.getInicioProyecto())) / 7) + 1;
        } else {
            semana = 0;
        }
        double valoraCalcular = proyectoConsultado.getValorGanado()-(proyectoConsultado.getValorPlaneado() * semana);
        System.out.println("Valor Calcular" + valoraCalcular);
        if (valoraCalcular == 0) {
            mensaje = "De acuerdo a lo planeado";
            mensaje2 = "Vas bien de tiempo";
            tipo = "info";
        } else if (valoraCalcular > 0) {
            mensaje = "Mejor que lo planeado";
            mensaje2 = "Vas adelantado";
            tipo = "success";
        } else {
            mensaje = "No se estan cumpliendo los objetivos";
            mensaje2 = "Vas con retraso de acuerdo a lo planeado";
            tipo = "warning";
        }

        respuestas.put("mensaje", mensaje);
        respuestas.put("mensaje2", mensaje2);
        respuestas.put("tipo", tipo);
        try {
            proyectoJ.put("respuesta", respuestas);

        } catch (JSONException e) {
            System.out.println("Error" + e);
        }
        Response.ResponseBuilder constructor = Response.ok(proyectoJ.toString());
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }

    @GET
    @Path("variaciondelCostoLider")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response variaciondelCostoLider(@QueryParam("idProyecto") String idProyecto) throws ParseException {
        JSONObject proyectoJ = null;
        DaoProyecto daoProyecto = new DaoProyecto();
        BeanProyecto proyectoConsultado = null;
        String mensaje2 = "";
        String actual = "";
        int semana = 0;
        int idProyectoConsultado = 0;
        try {
            proyectoJ = new JSONObject(idProyecto);
            idProyectoConsultado = proyectoJ.getInt("proyecto");

        } catch (JSONException ex) {
            System.out.println("Error" + ex);
        }

        proyectoConsultado = daoProyecto.consultarProyectoporId(idProyectoConsultado);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fin = proyectoConsultado.getFinalProyecto();
        java.util.Date fechaFin = sdf.parse(fin);
        Calendar fecha = new GregorianCalendar();
        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        actual = "" + año + "-" + (mes + 1) + "-" + dia;
        java.util.Date fechadateactual = sdf.parse(actual);

        if (fechadateactual.before(fechaFin)) {
            semana = ((daoProyecto.consultarDias(proyectoConsultado.getInicioProyecto())) / 7) + 1;
        } else {
            semana = 0;
        }
        double valoraCalcular = (proyectoConsultado.getValorPlaneado() * semana) - daoProyecto.consultarPresupuestoGastado(idProyectoConsultado);
        System.out.println("Valor a " + valoraCalcular);

        if (valoraCalcular == 0) {
            mensaje = "De acuerdo a lo planeado";
            mensaje2 = "El proyecto va de acuerdo";
            tipo = "info";
        } else if (valoraCalcular > 0) {
            mensaje = "Mejor que lo planeado";
            mensaje2 = "El proyecto esta siendo más barato a lo planeado";
            tipo = "success";
        } else {
            mensaje = "No se estan cumpliendo los objetivos";
            mensaje2 = "El proyecto esta siendo más caro a lo planeado";
            tipo = "warning";
        }

        respuestas.put("mensaje", mensaje);
        respuestas.put("mensaje2", mensaje2);
        respuestas.put("tipo", tipo);
        try {
            proyectoJ.put("respuesta", respuestas);

        } catch (JSONException e) {
            System.out.println("Error" + e);
        }
        Response.ResponseBuilder constructor = Response.ok(proyectoJ.toString());
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }

    @GET
    @Path("desempeniodelCronogranaLider")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response desempeniodelCronogranaLider(@QueryParam("idProyecto") String idProyecto) throws ParseException {
        JSONObject proyectoJ = null;
        DaoProyecto daoProyecto = new DaoProyecto();
        BeanProyecto proyectoConsultado = null;
        String mensaje2 = "";
        String actual = "";
        int semana = 0;
        int idProyectoConsultado = 0;
        try {
            proyectoJ = new JSONObject(idProyecto);
            idProyectoConsultado = proyectoJ.getInt("proyecto");

        } catch (JSONException ex) {
            System.out.println("Error" + ex);
        }
        proyectoConsultado = daoProyecto.consultarProyectoporId(idProyectoConsultado);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fin = proyectoConsultado.getFinalProyecto();
        java.util.Date fechaFin = sdf.parse(fin);
        Calendar fecha = new GregorianCalendar();
        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        actual = "" + año + "-" + (mes + 1) + "-" + dia;
        java.util.Date fechadateactual = sdf.parse(actual);
        double valoraCalcular = daoProyecto.consultarPresupuestoGastado(idProyectoConsultado) / (proyectoConsultado.getValorPlaneado()*semana);
        System.out.println("El valor a calcular en variaciondelCostoLider es " + valoraCalcular);
        System.out.println("El valor a pleado en variaciondelCostoLider es " + proyectoConsultado.getValorPlaneado());
        System.out.println("El valor ganado en variaciondelCostoLider es " + daoProyecto.consultarPresupuestoGastado(idProyectoConsultado));

        if (valoraCalcular == 0) {
            mensaje = "De acuerdo a lo planeado";
            mensaje2 = "El trabajo va deacuerdo a lo planeado";
            tipo = "info";
        } else if (valoraCalcular > 0) {
            mensaje = "Mejor que lo planeado";
            mensaje2 = "El trabajo es mayor al planeado ";
            tipo = "success";
        } else {
            mensaje = "El trabajo es menor que al planeado";
            mensaje2 = "Vas a trasado";
            tipo = "warning";
        }
        respuestas.put("mensaje", mensaje);
        respuestas.put("mensaje2", mensaje2);
        respuestas.put("tipo", tipo);
        try {
            proyectoJ.put("respuesta", respuestas);

        } catch (JSONException e) {
            System.out.println("Error" + e);
        }
        Response.ResponseBuilder constructor = Response.ok(proyectoJ.toString());
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }
    
    
    
    @GET
    @Path("desempeniodelCostoLider")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response desempeniodelCostoLider(@QueryParam("idProyecto") String idProyecto) throws ParseException {
        JSONObject proyectoJ = null;
        DaoProyecto daoProyecto = new DaoProyecto();
        BeanProyecto proyectoConsultado = null;
        String mensaje2 = "";
        String actual = "";
        int semana = 0;
        int idProyectoConsultado = 0;
        try {
            proyectoJ = new JSONObject(idProyecto);
            idProyectoConsultado = proyectoJ.getInt("proyecto");

        } catch (JSONException ex) {
            System.out.println("Error" + ex);
        }
        proyectoConsultado = daoProyecto.consultarProyectoporId(idProyectoConsultado);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fin = proyectoConsultado.getFinalProyecto();
        java.util.Date fechaFin = sdf.parse(fin);
        Calendar fecha = new GregorianCalendar();
        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        actual = "" + año + "-" + (mes + 1) + "-" + dia;
        java.util.Date fechadateactual = sdf.parse(actual);
        double valoraCalcular =  proyectoConsultado.getValorGanado()/daoProyecto.consultarPresupuestoGastado(idProyectoConsultado);
      

        if (valoraCalcular == 1) {
            mensaje = "De acuerdo a lo planeado";
            mensaje2 = "El costo va a lo planeado";
            tipo = "info";
        } else if (valoraCalcular < 1) {
            mensaje = "No se estan cumplieando las metas";
            mensaje2 = "El costo no va conforme a lo planeado";
            tipo = "warning";
        } else {
            mensaje = "El costo va a mejor a lo planeado";
            mensaje2 = "Haz gastado menos de lo planeado";
            tipo = "success";
        }
        respuestas.put("mensaje", mensaje);
        respuestas.put("mensaje2", mensaje2);
        respuestas.put("tipo", tipo);
        try {
            proyectoJ.put("respuesta", respuestas);

        } catch (JSONException e) {
            System.out.println("Error" + e);
        }
        Response.ResponseBuilder constructor = Response.ok(proyectoJ.toString());
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }
    
    
     @GET
    @Path("variacionCronogramaAdministrador")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response variacionCronogramaAdministrador() throws ParseException {
        JSONObject proyectoJ = new JSONObject();
        DaoProyecto daoProyecto = new DaoProyecto();
        BeanProyecto proyectoConsultado = null;
        String mensaje2 = "";
        String actual = "";
        int semana = 0;
        
        proyectoConsultado = daoProyecto.consultarProyectoporId(idProyectoGlobal);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fin = proyectoConsultado.getFinalProyecto();
        java.util.Date fechaFin = sdf.parse(fin);
        Calendar fecha = new GregorianCalendar();
        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        actual = "" + año + "-" + (mes + 1) + "-" + dia;
        java.util.Date fechadateactual = sdf.parse(actual);

        if (fechadateactual.before(fechaFin)) {
            semana = ((daoProyecto.consultarDias(proyectoConsultado.getInicioProyecto())) / 7) + 1;
        } else {
            semana = 0;
        }
        double valoraCalcular =  proyectoConsultado.getValorGanado()-(proyectoConsultado.getValorPlaneado() * semana);
        System.out.println("Valor Calcular" + valoraCalcular);
        if (valoraCalcular == 0) {
            mensaje = "De acuerdo a lo planeado";
            mensaje2 = "Vas bien de tiempo";
            tipo = "info";
        } else if (valoraCalcular > 0) {
            mensaje = "Mejor que lo planeado";
            mensaje2 = "Vas adelantado";
            tipo = "success";
        } else {
            mensaje = "No se estan cumpliendo los objetivos";
            mensaje2 = "Vas con retraso de acuerdo a lo planeado";
            tipo = "warning";
        }

        respuestas.put("mensaje", mensaje);
        respuestas.put("mensaje2", mensaje2);
        respuestas.put("tipo", tipo);
        try {
            proyectoJ.put("respuesta", respuestas);

        } catch (JSONException e) {
            System.out.println("Error" + e);
        }
        Response.ResponseBuilder constructor = Response.ok(proyectoJ.toString());
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }

    @GET
    @Path("variaciondelCostoAdministrador")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response variaciondelCostoAdministrador() throws ParseException {
        JSONObject proyectoJ = new JSONObject ();
        DaoProyecto daoProyecto = new DaoProyecto();
        BeanProyecto proyectoConsultado = null;
        String mensaje2 = "";
        String actual = "";
        int semana = 0;
        
        proyectoConsultado = daoProyecto.consultarProyectoporId(idProyectoGlobal);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fin = proyectoConsultado.getFinalProyecto();
        java.util.Date fechaFin = sdf.parse(fin);
        Calendar fecha = new GregorianCalendar();
        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        actual = "" + año + "-" + (mes + 1) + "-" + dia;
        java.util.Date fechadateactual = sdf.parse(actual);

        if (fechadateactual.before(fechaFin)) {
            semana = ((daoProyecto.consultarDias(proyectoConsultado.getInicioProyecto())) / 7) + 1;
        } else {
            semana = 0;
        }
        double valoraCalcular = (proyectoConsultado.getValorPlaneado() * semana) - daoProyecto.consultarPresupuestoGastado(idProyectoGlobal);
        System.out.println("Valor a " + valoraCalcular);

        if (valoraCalcular == 0) {
            mensaje = "De acuerdo a lo planeado";
            mensaje2 = "El proyecto va de acuerdo";
            tipo = "info";
        } else if (valoraCalcular > 0) {
            mensaje = "Mejor que lo planeado";
            mensaje2 = "El proyecto esta siendo más barato a lo planeado";
            tipo = "success";
        } else {
            mensaje = "No se estan cumpliendo los objetivos";
            mensaje2 = "El proyecto esta siendo más caro a lo planeado";
            tipo = "warning";
        }

        respuestas.put("mensaje", mensaje);
        respuestas.put("mensaje2", mensaje2);
        respuestas.put("tipo", tipo);
        try {
            proyectoJ.put("respuesta", respuestas);

        } catch (JSONException e) {
            System.out.println("Error" + e);
        }
        Response.ResponseBuilder constructor = Response.ok(proyectoJ.toString());
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }

    @GET
    @Path("desempeniodelCronogranaAdministrador")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response desempeniodelCronogranaAdministrador() throws ParseException {
        JSONObject proyectoJ = new JSONObject();
        DaoProyecto daoProyecto = new DaoProyecto();
        BeanProyecto proyectoConsultado = null;
        String mensaje2 = "";
        String actual = "";
        int semana = 0;
      
        proyectoConsultado = daoProyecto.consultarProyectoporId(idProyectoGlobal);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fin = proyectoConsultado.getFinalProyecto();
        java.util.Date fechaFin = sdf.parse(fin);
        Calendar fecha = new GregorianCalendar();
        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        actual = "" + año + "-" + (mes + 1) + "-" + dia;
        java.util.Date fechadateactual = sdf.parse(actual);
        double valoraCalcular = daoProyecto.consultarPresupuestoGastado(idProyectoGlobal) / (proyectoConsultado.getValorPlaneado()*semana);
        System.out.println("El valor a calcular en variaciondelCostoLider es " + valoraCalcular);
        System.out.println("El valor a pleado en variaciondelCostoLider es " + proyectoConsultado.getValorPlaneado());
        System.out.println("El valor ganado en variaciondelCostoLider es " + daoProyecto.consultarPresupuestoGastado(idProyectoGlobal));

        if (valoraCalcular == 0) {
            mensaje = "De acuerdo a lo planeado";
            mensaje2 = "El trabajo va deacuerdo a lo planeado";
            tipo = "info";
        } else if (valoraCalcular > 0) {
            mensaje = "Mejor que lo planeado";
            mensaje2 = "El trabajo es mayor al planeado ";
            tipo = "success";
        } else {
            mensaje = "El trabajo es menor que al planeado";
            mensaje2 = "Vas a trasado";
            tipo = "warning";
        }
        respuestas.put("mensaje", mensaje);
        respuestas.put("mensaje2", mensaje2);
        respuestas.put("tipo", tipo);
        try {
            proyectoJ.put("respuesta", respuestas);

        } catch (JSONException e) {
            System.out.println("Error" + e);
        }
        Response.ResponseBuilder constructor = Response.ok(proyectoJ.toString());
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }
    
    
    
    @GET
    @Path("desempeniodelCostoAdministrador")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response desempeniodelCostoAdministrador() throws ParseException {
        JSONObject proyectoJ = new JSONObject();
        DaoProyecto daoProyecto = new DaoProyecto();
        BeanProyecto proyectoConsultado = null;
        String mensaje2 = "";
        String actual = "";
        int semana = 0;
       
        proyectoConsultado = daoProyecto.consultarProyectoporId(idProyectoGlobal);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fin = proyectoConsultado.getFinalProyecto();
        java.util.Date fechaFin = sdf.parse(fin);
        Calendar fecha = new GregorianCalendar();
        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        actual = "" + año + "-" + (mes + 1) + "-" + dia;
        java.util.Date fechadateactual = sdf.parse(actual);
        double valoraCalcular =  proyectoConsultado.getValorGanado()/daoProyecto.consultarPresupuestoGastado(idProyectoGlobal);
      

        if (valoraCalcular == 1) {
            mensaje = "De acuerdo a lo planeado";
            mensaje2 = "El costo va a lo planeado";
            tipo = "info";
        } else if (valoraCalcular < 1) {
            mensaje = "No se estan cumplieando las metas";
            mensaje2 = "El costo no va conforme a lo planeado";
            tipo = "warning";
        } else {
            mensaje = "El costo va a mejor a lo planeado";
            mensaje2 = "Haz gastado menos de lo planeado";
            tipo = "success";
        }
        respuestas.put("mensaje", mensaje);
        respuestas.put("mensaje2", mensaje2);
        respuestas.put("tipo", tipo);
        try {
            proyectoJ.put("respuesta", respuestas);

        } catch (JSONException e) {
            System.out.println("Error" + e);
        }
        Response.ResponseBuilder constructor = Response.ok(proyectoJ.toString());
        constructor.header("Access-Control-Allow-Origin", "*");
        constructor.header("Access-Control-Allow-Methods", "*");
        return constructor.build();
    }

}
