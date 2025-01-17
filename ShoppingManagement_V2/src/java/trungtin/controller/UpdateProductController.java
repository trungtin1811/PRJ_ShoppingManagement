/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trungtin.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import trungtin.product.ProductDAO;
import trungtin.product.ProductDTO;
import trungtin.product.ProductError;
import trungtin.user.UserDAO;
import trungtin.user.UserDTO;

/**
 *
 * @author User
 */
@MultipartConfig
public class UpdateProductController extends HttpServlet {

    private static final String ERROR = "updateProduct.jsp";
    private static final String SUCCESS = "MainController?btnAction=SearchProduct&searchValue=";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String url = ERROR;
        try {
            String productID = request.getParameter("productID");
            String productName = request.getParameter("productName");
            double price = Double.parseDouble(request.getParameter("price"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            String categoryID = request.getParameter("categoryID");
            Date importDate = new java.sql.Date((formatter.parse(request.getParameter("importDate")).getTime()));
            Date usingDate = new java.sql.Date((formatter.parse(request.getParameter("usingDate")).getTime()));
            Part part = request.getPart("image");

            String realPath = request.getServletContext().getRealPath("/images");
            String fileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
            if (!Files.exists(Paths.get(realPath))) {
                Files.createDirectories(Paths.get(realPath));
            }
            if (!fileName.equals("")) {
                part.write(realPath + "/" + fileName);
            }
            String image = "images/" + fileName;
            boolean checkValidation = true;

            ProductError productError = new ProductError();
            ProductDAO dao = new ProductDAO();            
            if (productID.length() < 2 || productID.length() > 10) {
                productError.setProductIDError("Product ID length must be in [2;10]!!");
                checkValidation = false;
            }
            if (productName.length() < 2 || productName.length() > 20) {
                productError.setProductNameError("Full Name length must be in [2;20]!!");
                checkValidation = false;
            }
            request.setAttribute("PRODUCT_ERROR", productError);

//            chưa xong
            if (checkValidation) {
                HttpSession session = request.getSession();
                ProductDTO product = (ProductDTO) session.getAttribute("UPDATE_PRODUCT");
                if (!productName.equals("")) {
                    product.setProductName(productName);
                }
                if (!image.equals("images/")) {
                    product.setImage(image);
                }
                if (price != 0) {
                    product.setPrice(price);
                }
                if (quantity != 0) {
                    product.setQuantity(quantity);
                }
                if (!importDate.equals("")) {
                    product.setImportDate(importDate);
                }
                if (!usingDate.equals("")) {
                    product.setUsingDate(usingDate);
                }
                if (!categoryID.equals("")) {
                    product.setCategoryID(categoryID);
                }
                boolean check = dao.updateProduct(product);
                if (check) {
                    url = SUCCESS;
                    request.setAttribute("MESSAGE", "Update Successfully!!");
                    session.setAttribute("UPDATE_PRODUCT", product);
                }
            }
        } catch (Exception e) {
            log("Error at LoginController: " + e.toString());
        } finally {
            request.getRequestDispatcher(url).forward(request, response);
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
