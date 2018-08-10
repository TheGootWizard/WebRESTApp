package services;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.BasicUser;
import beans.Operator;
import beans.User;
import dao.AdminDAO;
import dao.BasicUserDAO;
import dao.OperatorDAO;

@Path("")
public class LoginService {

	@Context
	ServletContext ctx;
	String contextPath;


	public LoginService() {

	}

	@PostConstruct
	public void init() {

		if (ctx.getAttribute("userDAO") == null) {
			contextPath = ctx.getRealPath("");
			ctx.setAttribute("userDAO", new BasicUserDAO(contextPath));
		}
	}

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(User ud, @Context HttpServletRequest request) throws URISyntaxException {
		BasicUserDAO userDao = (BasicUserDAO) ctx.getAttribute("userDAO");
		OperatorDAO operatorDao = (OperatorDAO) ctx.getAttribute("operatorDAO");
		AdminDAO adminDao = (AdminDAO) ctx.getAttribute("adminDAO");
		User loggedUser = userDao.find(ud.getUsername().toLowerCase(), ud.getPassword());
		if (loggedUser == null) {
			loggedUser = operatorDao.find(ud.getUsername().toLowerCase(), ud.getPassword());
			if (loggedUser == null) {
				loggedUser = adminDao.find(ud.getUsername().toLowerCase(), ud.getPassword());
				if (loggedUser == null) {
					return Response.status(400).entity("Invalid username and/or password").build();
				}
			}		
		}

		request.getSession().setAttribute("user", loggedUser);
		return Response.status(200).build();
	}

	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(BasicUser ud, @Context HttpServletRequest request) {

		BasicUserDAO userDao = (BasicUserDAO) ctx.getAttribute("userDAO");
		OperatorDAO operatorDao = (OperatorDAO) ctx.getAttribute("operatorDAO");
		AdminDAO adminDao = (AdminDAO) ctx.getAttribute("adminDAO");
		contextPath = ctx.getRealPath("");

		if (ud.getEmail() == null || ud.getPassword() == null || ud.getCountry() == null || ud.getUsername() == null) {
			return Response.status(400).entity("Invalid request!").build();
		}

		if (!ud.getUsername().matches("^(?=.{3,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$")) {
			return Response.status(400).entity("Invalid username format!").build();
		}

		if (ud.getEmail().matches("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\\\.[A-Z]{2,6}$")) {
			return Response.status(400).entity("Invalid email format!").build();
		}

		for (BasicUser u : userDao.findAll()) {
			if (u.getEmail().equals(ud.getEmail())) {
				return Response.status(400).entity("Email is already in use!").build();
			}
		}
		
		if(operatorDao.find(ud.getUsername()) != null) {
			return Response.status(400).entity("Username already exists").build();			
		}
		
		if(adminDao.find(ud.getUsername()) != null) {
			return Response.status(400).entity("Username already exists").build();			
		}

		BasicUser nUser = new BasicUser(ud.getUsername().toLowerCase(), ud.getPassword(), ud.getEmail(), ud.getCountry());
		if (userDao.add(nUser)) {
			return Response.ok(nUser).build();

		} else {
			return Response.status(400).entity("Username already exists").build();
		}

	}
	
	@POST
	@Path("/add-operator")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addOperator(Operator ud, @Context HttpServletRequest request) {

		BasicUserDAO userDao = (BasicUserDAO) ctx.getAttribute("userDAO");
		OperatorDAO operatorDao = (OperatorDAO) ctx.getAttribute("operatorDAO");
		AdminDAO adminDao = (AdminDAO) ctx.getAttribute("adminDAO");
		
		contextPath = ctx.getRealPath("");

		if (ud.getPassword() == null || ud.getUsername() == null) {
			return Response.status(400).entity("Invalid request!").build();
		}

		if (!ud.getUsername().matches("^(?=.{3,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$")) {
			return Response.status(400).entity("Invalid username format!").build();
		}
		
		if(userDao.find(ud.getUsername()) != null) {
			return Response.status(400).entity("Username already exists").build();			
		}
		
		if(adminDao.find(ud.getUsername()) != null) {
			return Response.status(400).entity("Username already exists").build();			
		}

		Operator nOp = new Operator(ud.getUsername().toLowerCase(), ud.getPassword());
		if (operatorDao.add(nOp)) {
			return Response.ok(nOp).build();

		} else {
			return Response.status(400).entity("Username already exists").build();
		}

	}

	@GET
	@Path("/logout")
	public Response logout(@Context HttpServletRequest request) throws URISyntaxException {
		request.getSession().invalidate();
		java.net.URI location = new java.net.URI("../index.html");
		return Response.temporaryRedirect(location).build();
	}

	@GET
	@Path("/user")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@Context HttpServletRequest request) {
		ArrayList<User> usr = new ArrayList<User>();
		usr.add((User) request.getSession().getAttribute("user"));
		//return usr;
		return Response.ok((User) request.getSession().getAttribute("user")).build();
	}

	@GET
	@Path("/customers")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<User> getCustomers(@Context HttpServletRequest request) {
		BasicUserDAO dao = (BasicUserDAO) ctx.getAttribute("userDAO");
		ArrayList<User> cust = new ArrayList<User>();
		for (User u : dao.findAll()) {
			if (u.getuType()==0) {
				cust.add(u);
			}
		}
		return cust;
	}

	@GET
	@Path("/sellers")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<User> getSellers(@Context HttpServletRequest request) {
		BasicUserDAO dao = (BasicUserDAO) ctx.getAttribute("userDAO");
		ArrayList<User> sell = new ArrayList<User>();
		for (User u : dao.findAll()) {
			if (u.getuType()==1) {
				sell.add(u);
			}
		}
		return sell;
	}

	@DELETE
	@Path("/delete-user")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeUser(@Context HttpServletRequest request) {
		BasicUserDAO dao = (BasicUserDAO) ctx.getAttribute("userDAO");
		User current = (User) request.getSession().getAttribute("user");
		
		if (current.getuType()!=0 && current.getuType()!=1) {
			return Response.status(400).build();

		} else {
			dao.remove(current.getUsername());
			return Response.status(200).build();

		}
	}

	@PUT
	@Path("/promote")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response editUser(User us, @Context HttpServletRequest request) {
		BasicUserDAO dao = (BasicUserDAO) ctx.getAttribute("userDAO");
		User target = dao.find(us.getUsername());
		User current = (User) request.getSession().getAttribute("user");

		if (! (current.getuType() == 3)) {
			return Response.status(400).build();
		}
		if (target == null) {
			return Response.status(400).build();
		} else {
			target.setuType(1);
			return Response.status(200).build();

		}
	}

	@GET
	@Path("/users")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<BasicUser> getUsers(@Context HttpServletRequest request) {
		User current = (User) request.getSession().getAttribute("user");

		if (current == null) {
			return null;
		}

		if (current.getuType() == 3) {
			return ((BasicUserDAO) ctx.getAttribute("userDAO")).findAll();
		} else {
			return null;
		}

	}
}
