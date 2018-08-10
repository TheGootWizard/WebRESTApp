package listeners;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import beans.Admin;
import dao.AdminDAO;
import dao.BasicUserDAO;
import dao.DAOData;
import dao.OperatorDAO;
import dao.PhotoDAO;

public class Listener implements javax.servlet.ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		ServletContext ctx = arg0.getServletContext();
		AdminDAO aDAO = (AdminDAO) ctx.getAttribute("adminDAO");
		OperatorDAO oDAO = (OperatorDAO) ctx.getAttribute("operatorDAO");
		PhotoDAO pDAO = (PhotoDAO) ctx.getAttribute("photoDAO");
		BasicUserDAO uDAO = (BasicUserDAO) ctx.getAttribute("userDAO");

		if (aDAO == null) {
			aDAO = new AdminDAO();
		}

		if (oDAO == null) {
			oDAO = new OperatorDAO();
		}

		if (pDAO == null) {
			pDAO = new PhotoDAO();
		}

		if (uDAO == null) {
			uDAO = new BasicUserDAO();
		}

		// String path = arg0.getServletContext().getRealPath("/") + "data.json";
		String path = "C:\\Users\\Anagnosti\\Desktop\\Front End\\data\\" + "data.json";
		ObjectMapper mapper = new ObjectMapper();

		// SimpleModule simpleModule = new SimpleModule();
		// simpleModule.addKeyDeserializer(Article.class, new ArticleKeyDeserializer());
		// mapper.registerModule(simpleModule);

		DAOData data = new DAOData(aDAO, oDAO, pDAO, uDAO);
		System.out.println(data.getaDAO().findAll());

		try {
			mapper.writeValue(new File(path), data);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		ObjectMapper mapper = new ObjectMapper();
		ServletContext ctx = arg0.getServletContext();

		// SimpleModule simpleModule = new SimpleModule();
		// simpleModule.addKeyDeserializer(Article.class, new ArticleKeyDeserializer());
		// mapper.registerModule(simpleModule);

		// String path = arg0.getServletContext().getRealPath("/") + "data.json";
		String path = "C:\\Users\\Anagnosti\\Desktop\\Front End\\data\\" + "data.json";

		try {
			DAOData data = mapper.readValue(new File(path), DAOData.class);

			if (data.getuDAO() != null) {
				ctx.setAttribute("userDAO", data.getuDAO());
			} else {
				ctx.setAttribute("userDAO", new BasicUserDAO());
			}

			if (data.getaDAO() != null) {
				if (data.getaDAO().findAll().isEmpty()) {
					data.getaDAO().add(new Admin("admin", "admin"));
				}
				ctx.setAttribute("adminDAO", data.getaDAO());
			} else {
				AdminDAO aDAO = new AdminDAO();
				aDAO.add(new Admin("admin", "admin"));
				ctx.setAttribute("adminDAO", aDAO);
			}

			if (data.getoDAO() != null) {
				ctx.setAttribute("operatorDAO", data.getoDAO());
			} else {
				ctx.setAttribute("operatorDAO", new OperatorDAO());
			}

			if (data.getpDAO() != null) {
				ctx.setAttribute("photoDAO", data.getpDAO());
			} else {
				ctx.setAttribute("photoDAO", new PhotoDAO());
			}

		} catch (JsonParseException e) {
			e.printStackTrace();
			ctx.setAttribute("userDAO", new BasicUserDAO());

			AdminDAO aDAO = new AdminDAO();
			aDAO.add(new Admin("admin", "admin"));

			ctx.setAttribute("adminDAO", aDAO);
			ctx.setAttribute("operatorDAO", new OperatorDAO());
			ctx.setAttribute("photoDAO", new PhotoDAO());
		} catch (JsonMappingException e) {
			e.printStackTrace();
			ctx.setAttribute("userDAO", new BasicUserDAO());

			AdminDAO aDAO = new AdminDAO();
			aDAO.add(new Admin("admin", "admin"));

			ctx.setAttribute("adminDAO", aDAO);
			ctx.setAttribute("operatorDAO", new OperatorDAO());
			ctx.setAttribute("photoDAO", new PhotoDAO());
		} catch (IOException e) {
			e.printStackTrace();

			ctx.setAttribute("userDAO", new BasicUserDAO());

			AdminDAO aDAO = new AdminDAO();
			aDAO.add(new Admin("admin", "admin"));

			ctx.setAttribute("adminDAO", aDAO);
			ctx.setAttribute("operatorDAO", new OperatorDAO());
			ctx.setAttribute("photoDAO", new PhotoDAO());

		}

	}

}
