

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author owen
 */


@WebServlet(name = "StartPageServlet", urlPatterns = {"/StartPageServlet"})
public class StartPageServlet extends HttpServlet {
    
    @Resource(name = "jdbc/HW2DB")
    private javax.sql.DataSource datasource;
    
   /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */


 
    
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List votes = new ArrayList();
        int votesThisSession = 0;
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {


            
            Connection connection = datasource.getConnection();
                        
            
            String addType = request.getParameter("name");
            if (request.getParameter("submitType") != null && addType != null && addType.length()>0)
            {                
                int vote = 1;
                String insertSQL = "insert into VOTES (musictype,numvotes) values (?,?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertSQL);
                insertStatement.setString(1, addType);
                insertStatement.setInt(2, vote);
                int recordsAffected = insertStatement.executeUpdate();
                insertStatement.close();
                
                String sql = "select * from VOTES";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();
                votesThisSession++;
                while (resultSet.next())
                {
                    String current = resultSet.getString("musictype");
                    int currentVotes = resultSet.getInt("numvotes");
                    votes.add(current);
                    votes.add(currentVotes);
                    
                }
                resultSet.close();
                
                HttpSession session = request.getSession();
                if(session.getAttribute("votesThisSession")==null)
                {
                    session.setAttribute("votesThisSession",votesThisSession);
                }
                else
                {
                    votesThisSession += (int)session.getAttribute("votesThisSession");
                    session.setAttribute("votesThisSession", votesThisSession);
                }
                
                request.setAttribute("votes", votes);
                request.getRequestDispatcher("DisplayVotes").forward(request, response);
            }
            if (request.getParameter("submitVotes") != null && request.getParameterValues("votes") != null)
            {
                
                String[] tallyVotes = request.getParameterValues("votes");
                for(int i = 0; i < tallyVotes.length; i++)
                {
                    String getnumsql = "select numvotes from votes where musictype like ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(getnumsql);
                    preparedStatement.setString(1,tallyVotes[i]);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if(resultSet.next()){
                        int currentVote = resultSet.getInt("numvotes") + 1;
                        String insertSQL = "update VOTES SET numvotes=? WHERE musictype=?";
                        PreparedStatement editStatement = connection.prepareStatement(insertSQL);
                        editStatement.setInt(1,currentVote);
                        editStatement.setString(2,tallyVotes[i]);
                        int recordsAffected = editStatement.executeUpdate();
                        editStatement.close();
                    }              
                    resultSet.close();
                    preparedStatement.close();
                    votesThisSession++;
                }
            
            String sql = "select * from VOTES";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                String current = resultSet.getString("musictype");
                int currentVotes = resultSet.getInt("numvotes");
                votes.add(current);
                votes.add(currentVotes);
                                
            }
            resultSet.close();
            preparedStatement.close();
            HttpSession session = request.getSession();
            if(session.getAttribute("votesThisSession")==null)
            {
                session.setAttribute("votesThisSession",votesThisSession);
            }
            else
            {
                votesThisSession += (int)session.getAttribute("votesThisSession");
                session.setAttribute("votesThisSession", votesThisSession);
            }
            request.setAttribute("votes", votes);
            request.getRequestDispatcher("DisplayVotes").forward(request, response);
            }
               

            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Results</title>");            
            out.println("<meta charset=\"UTF-8\">");
            out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
            out.println("</head>");
            out.println("<body>");
            out.println("<form name='voteList' >");
            out.println("<div>Vote what your favorite type of music is:</div>");
            out.println("<div id='Music'>");
            
            String sql = "select * from VOTES";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            int asdf = 0;
            while (resultSet.next())
            {
                String current = resultSet.getString("musictype");
                int currentVotes = resultSet.getInt("numvotes");
                votes.add(currentVotes);
                out.println("<input type='checkbox' name ='votes' value='"+current+"'>"+current+"<br/>");
                asdf++;
            }
            resultSet.close();
            preparedStatement.close();
            
            out.println("</div>");
            out.println("<input type='submit' name ='submitVotes' value='Submit vote'>");
            out.println("</form>");
            out.println("<form name='addMusic'>");
            out.println("<br/>");
            out.println("<div>Or add a new one</div>");
            out.println("<br/>");
            out.println("New music type:<input type='text' name='name' /><br/>");
            out.println("<input type='submit' name ='submitType' value='Add type and vote'>");
            
            out.println("<br/>");
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
            

            connection.close();
            
            
        } catch (Exception e) {
            out.println("error occurred " + e.getMessage());
        } finally {
            out.close();
        }

    }


    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
