<%@ page import="com.eddie.ecommerce.service.*, com.eddie.web.controller.*, java.util.List, com.eddie.ecommerce.model.*,java.util.Map" %>
<%@include file="/html/common/header.jsp"%>


	<section class="sjuego">
		<% 
			
			Juego resultados =(Juego) request.getAttribute(AttributeNames.PRODUCTO_RESULTADOS);
			Map<Usuario, ItemBiblioteca> comentarios= (Map<Usuario, ItemBiblioteca>) request.getAttribute(AttributeNames.COMENTARIOS_JUEGO);
			List<Juego> resultadosBiblioteca = (List<Juego>) request.getAttribute(AttributeNames.BIBLIOTECA_RESULTADOS);		
			
		%>
		<img src="<%=request.getContextPath()%>/imgs/icojuego/<%=resultados.getIdJuego()%>.jpg"></img>
		<h1><%=resultados.getNombre()%></h1>
		<p><%=resultados.getFechaLanzamiento() %></p>
		
		<% if(u!=null){
						Boolean mostrar=false;
						for(Juego j:resultadosBiblioteca){
							if(resultados.getIdJuego()==j.getIdJuego()){
								mostrar=true;
							}
						}
						if(mostrar==true){
							%>
								<p class="a">Ya esta A�adido</p>
							<% 
						}else if(mostrar==false){
							%>
								<a class="a" href="<%=ControllerPaths.BIBLIOTECA%>?
									<%=ParameterNames.ACTION%>=<%=Actions.ADDJUEGO%>&amp;<%=ParameterNames.ID%>=
									<%=resultados.getIdJuego()%>"><button>A�adir a la Biblioteca</button></a>
							<% 
								
						}
						%>
						
		<%}
 
			comentarios.forEach((k,v) -> {
				%>
				<div class="comentario">
					<p><%=k.getNombreUser()%></p>
					<p><%=v.getFechaComentario()%></p>
					<p><%=v.getComentario()%></p>
				</div>
				<%
			});
		
		if(u!=null){
			%>
			<div class="comentario">
				<p>Tu comentario <%=u.getNombreUser()%> :</p>
				<textarea rows="10" cols="50" name=""><%=comentarios.get(u.getNombreUser()).getComentario()%></textarea>
				<a href="">Modificar</a>			
			</div>
			<%
		}
	     
		
		%>
				
	</section>
	
<%@include file="/html/common/footer.jsp"%>