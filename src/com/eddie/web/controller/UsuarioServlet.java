package com.eddie.web.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.eddie.ecommerce.exceptions.DataException;
import com.eddie.ecommerce.model.Pais;
import com.eddie.ecommerce.model.Usuario;
import com.eddie.ecommerce.service.PaisService;
import com.eddie.ecommerce.service.UsuarioService;
import com.eddie.ecommerce.service.impl.PaisServiceImpl;
import com.eddie.ecommerce.service.impl.UsuarioServiceImpl;
import com.eddie.web.model.Errors;
import com.eddie.web.controller.Actions;
import com.eddie.web.controller.AttributeNames;
import com.eddie.web.util.DateUtils;
import com.eddie.web.util.ErrorCodes;
import com.eddie.web.controller.ParameterNames;
import com.eddie.web.util.SessionAttributeNames;
import com.eddie.web.util.SessionManager;
import com.eddie.web.util.Validacion;
import com.eddie.web.controller.ViewPaths;

/**
 * Servlet implementation class UsuarioServlet
 */
@WebServlet("/usuario")
public class UsuarioServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static Logger logger = LogManager.getLogger(UsuarioServlet.class);
	
	private UsuarioService userv=null;
	private PaisService pserv=null; 
	
    public UsuarioServlet() {
        super();
        userv=new UsuarioServiceImpl();
        pserv=new PaisServiceImpl();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String action = request.getParameter(ParameterNames.ACTION);

			if (logger.isDebugEnabled()) {
				logger.debug("Action {}: {}", action, ToStringBuilder.reflectionToString(request.getParameterMap()));
			}
			
			 
			Errors errors = new Errors(); 
			String target = null;
			boolean redirect=false;
			
			if (Actions.LOGIN.equalsIgnoreCase(action)) {
				
				// Recuperacion
				String email = request.getParameter(ParameterNames.EMAIL);
				String password = request.getParameter(ParameterNames.PASSWORD);
				
				String emailValid = Validacion.validEmail(email);
				String passwordValid = Validacion.validPassword(password);
				
				if (StringUtils.isEmpty(emailValid)) {
					errors.add(ParameterNames.EMAIL,ErrorCodes.MANDATORY_PARAMETER);
				}
				
				Usuario usuario = null;
				if (!errors.hasErrors()) {
					
						usuario = userv.login(emailValid, passwordValid);
					
				}
				if(usuario == null) {
					errors.add(ParameterNames.ACTION,ErrorCodes.AUTHENTICATION_ERROR);
				}
				
				if (errors.hasErrors()) {	
					if (logger.isDebugEnabled()) {
						logger.debug("Autenticacion fallida: {}", errors);
					}				
					request.setAttribute(AttributeNames.ERRORS, errors);				
					target = ViewPaths.LOGIN;				
				} else {				
					SessionManager.set(request, SessionAttributeNames.USER, usuario);		
					target = request.getContextPath()+ViewPaths.HOME;
					redirect=true;
				}
				
			}else if (Actions.LOGOUT.equalsIgnoreCase(action)) {
				SessionManager.set(request, SessionAttributeNames.USER, null);
				target = request.getContextPath()+ViewPaths.HOME;
				redirect=true;
			} else if(Actions.PREREGISTRO.equalsIgnoreCase(action)){
				List<Pais> paises = pserv.findAll();
				request.setAttribute(AttributeNames.PAISES, paises);
				target = ViewPaths.REGISTRO;
			}else if(Actions.REGISTRO.equalsIgnoreCase(action)) {
				String nombre=request.getParameter(ParameterNames.NOMBRE);
				String apellido1=request.getParameter(ParameterNames.APELLIDO1);
				String apellido2=request.getParameter(ParameterNames.APELLIDO2);
				String email=request.getParameter(ParameterNames.EMAIL);
				String telefono=request.getParameter(ParameterNames.TELEFONO);
				String password=request.getParameter(ParameterNames.PASSWORD);
				String fecha=request.getParameter(ParameterNames.FECHANACIMIENTO);
				String genero=request.getParameter(ParameterNames.GENERO);
				
				Usuario u= new Usuario();
				
				SimpleDateFormat sdf=(SimpleDateFormat) DateUtils.FORMATODATA;
				Date fechaformat=sdf.parse(fecha);
				
				u.setNombre(Validacion.validNombre(nombre));
				u.setApellido1(Validacion.validApellido1(apellido1));
				u.setApellido2(Validacion.validApellido2(apellido2));
				u.setEmail(Validacion.validEmail(email));
				u.setTelefono(Validacion.validTelefono(telefono));
				u.setPassword(Validacion.validPassword(password));
				u.setFechaNacimiento(fechaformat);
				u.setGenero(Validacion.validGenero(genero));
				u.setNombreUser(nombre+apellido1.charAt(0)+apellido2.charAt(0));
				
				userv.create(u);
				target = request.getContextPath()+ViewPaths.LOGIN;
				redirect=true;
			}else {
				// Mmm...
				logger.error("Action desconocida");
				// target ?
			}
			if(redirect==true) {
				logger.info("Redirect to "+target);
				response.sendRedirect(target);
			}else {
				logger.info("forwarding to "+target);
				request.getRequestDispatcher(target).forward(request, response);
			}
			} catch (SQLException e) {	
				e.printStackTrace();
			} catch (DataException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
