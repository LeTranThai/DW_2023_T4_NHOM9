<%@ page import="warehouse.com.vn.model.PriceGold" %>
<%@ page import="java.util.List" %>
<%@ page import="warehouse.com.vn.model.Province_dim" %>
<%@ page import="warehouse.com.vn.service.PriceGoldService" %>
<%@ page import="warehouse.com.vn.model.Date" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
<%
    //  Get data from PriceGoldService
    List<Date> listDate = PriceGoldService.getAllDate();
    List<Province_dim> provinceDims = PriceGoldService.getAllProvince();
    List<PriceGold> priceGoldList = PriceGoldService.getAllGoldIsNew(19, listDate.get(listDate.size() - 1).getId());
%>

<div class="viewgoldprice">
    <span>Xem giá vàng tại đây</span>
</div>
<div class="province">
    <!--  Select province -->
    <label for="province">Chọn khu vực:</label>
    <select id="province" onchange="filter()">
        <%
            for (Province_dim provinceDim : provinceDims) {
        %>
        <%--           Display the list Province--%>
        <option value="<%= provinceDim.getId() %>"><%= provinceDim.getName() %>
        </option>
        <%
            }
        %>
    </select>

    <!-- Select date -->
    <div>
        <label for="date">Chọn ngày:</label>
        <select id="date" onchange="filter()">
            <%
                for (Date d : listDate) {
            %>
            <%--           Display the list Date--%>
            <option value="<%=d.getId() %>"><%= PriceGold.convertTimestampToString(d.getDate(), "yyyy-MM-dd hh:mm:ss") %>
            </option>
            <%
                }
            %>
        </select>
    </div>

    <!--  Display the selected date for update -->
    <h2>Cập nhật ngày:
        <p id="dateUpdate"><%= PriceGold.convertTimestampToString(priceGoldList.get(0).getStart_date(), "yyyy-MM-dd hh:mm:ss") %>
        </p>
    </h2>

    <!-- Display the gold price table -->
    <table>
        <thead>
        <tr>
            <th>Loại vàng | ĐVT: 1.000đ/Chỉ</th>
            <th>Giá mua</th>
            <th>Giá bán</th>
        </tr>
        </thead>
        <tbody class="content-price" id="province-table">
        <!--Display the list gold price laster -->
        <%
            for (PriceGold p : priceGoldList) {
        %>
        <tr class="province-row" data-province>
            <td><%= PriceGoldService.getTypeGold(p.getIdType()) %>
            </td>
            <td><span><%= PriceGold.formatPrice(p.getPriceBuy()) %></span></td>
            <td><span><%= PriceGold.formatPrice(p.getPriceSell()) %></span></td>
        </tr>
        <%}%>
        </tbody>
    </table>
</div>

<script>
    // JavaScript function to handle filtering
    function filter() {
        //Get necessary elements
        var goldTable = document.getElementById("province-table");
        var select = document.getElementById("province");
        var selectedValue = select.options[select.selectedIndex].value;
        var idDate = document.getElementById("date");
        var selectedIdDate = idDate.options[idDate.selectedIndex].value;
        var dateUpdate = document.getElementById("dateUpdate");
        var selectedDateName = idDate.options[idDate.selectedIndex].text;

        //  Make an AJAX request to the server-side controller
        var xhr = new XMLHttpRequest();
        xhr.open("GET", "PriceGoldController?selectedValue=" + selectedValue + "&selectedIdDate=" + selectedIdDate, true);
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 200) {
                //  Update the gold table and display the selected date
                goldTable.innerHTML = this.responseText;
                dateUpdate.innerHTML = selectedDateName;
            }
        };
        xhr.send();
    }
</script>

</body>
</html>
