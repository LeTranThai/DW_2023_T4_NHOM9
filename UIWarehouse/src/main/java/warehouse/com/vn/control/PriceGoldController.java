package warehouse.com.vn.control;

import com.google.gson.Gson;
import warehouse.com.vn.model.PriceGold;
import warehouse.com.vn.model.Province_dim;
import warehouse.com.vn.service.PriceGoldService;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "PriceGoldController", value = "/PriceGoldController")
public class PriceGoldController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//Get values from user
        String provinceId = request.getParameter("selectedValue");
        String selectedIdDate = request.getParameter("selectedIdDate");
//        Get list gold  by values from user
        List<PriceGold> goldPrices = PriceGoldService.getAllGoldByDateAndByProvince(Integer.parseInt(provinceId), Integer.parseInt(selectedIdDate));
        String htmlTableGold = "";
// Get list goldPrices format character
        for (PriceGold pg : goldPrices) {
            htmlTableGold += "  <tr class=\"province-row\" data-province>\n" +
                    "            <td>" + PriceGoldService.getTypeGold(pg.getIdType()) + "\n" +
                    "            </td>\n" +
                    "            <td><span>" + PriceGold.formatPrice(pg.getPriceBuy()) + "</span></td>\n" +
                    "            <td><span>" + PriceGold.formatPrice(pg.getPriceSell()) + "</span></td>\n" +
                    "        </tr>";
        }
        //Format UTF-8
        response.setCharacterEncoding("UTF-8");
        // Send data to Client
        response.getWriter().write(htmlTableGold);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
