<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <script type="text/javascript">
    /**
     * Create a new HTML DOM option object.
     */
    function createOption(value, text) {
      var new_option = document.createElement('option');
      new_option.setAttribute('value', value);
      new_option.text = text;
      return new_option;
    }

    /**
     * Add a new row for one asset into the 'assets_container' table.
     */
    function addAssetTable() {
      var assets_number = document.getElementsByName('assets_number');
      var number = parseInt(assets_number[0].getAttribute('value'));
      var assets_container = document.getElementById('assets_container');
      var new_asset_row = document.createElement('tr');
      var new_asset_name_td = document.createElement('td');
      var new_asset_mime_type_td = document.createElement('td');
      var new_asset_data_td = document.createElement('td');

      new_asset_name_td.appendChild(document.createTextNode('Relative Path: '));
      var new_asset_name = document.createElement('input');
      new_asset_name.setAttribute('type', 'text');
      new_asset_name.setAttribute('name', 'asset_name_' + number);
      new_asset_name.setAttribute('value', '');
      new_asset_name_td.appendChild(new_asset_name);

      new_asset_name_td.appendChild(document.createTextNode('Mime Type: '));
      var new_asset_mime_type = document.createElement('select');
      new_asset_mime_type.setAttribute('name', 'asset_mime_type_' + number);
      new_asset_mime_type_td.appendChild(new_asset_mime_type);

      new_asset_mime_type.add(createOption('application/pdf', 'PDF'));
      new_asset_mime_type.add(createOption('image/bmp', 'BMP'));
      new_asset_mime_type.add(createOption('image/gif', 'GIF'));
      new_asset_mime_type.add(createOption('image/jpeg', 'JPEG'));
      new_asset_mime_type.add(createOption('image/png', 'PNG'));
      new_asset_mime_type.add(createOption('text/html', 'HTML'));
      new_asset_mime_type.add(createOption('text/plain', 'TXT'));

      new_asset_data_td.appendChild(document.createTextNode('Data: '));
      var new_asset_data = document.createElement('input');
      new_asset_data.setAttribute('type', 'file');
      new_asset_data.setAttribute('name', 'asset_data_' + number);
      new_asset_data_td.appendChild(new_asset_data);

      new_asset_row.appendChild(new_asset_name_td);
      new_asset_row.appendChild(new_asset_mime_type_td);
      new_asset_row.appendChild(new_asset_data_td);
      assets_container.appendChild(new_asset_row);

      assets_number[0].setAttribute('value', number + 1);
    }
  </script>
</head>
<body>
  <form method="post" action="convert" enctype="multipart/form-data">
    <span style="font-size:150%"><b>Convert file</b></span><br><br>
    Upload File: <input name="upload_file" type="file" style="height:28px;width:500px"><br><br>
    Input File Type:
    <select name="input_type">
      <option value="application/pdf">PDF</option>
      <option value="image/bmp">BMP</option>
      <option value="image/gif">GIF</option>
      <option value="image/jpeg">JPEG</option>
      <option value="image/png">PNG</option>
      <option value="text/html">HTML</option>
      <option value="text/plain">TXT</option>
    </select>
    <br><br>
    Additional Assets: <input type="button" value="Add" onclick="addAssetTable()"><br><br>
    <table id="assets_container"></table><br>
    <input name="assets_number" type="hidden" value="1">
    Output File Type:
    <select name="output_type">
      <option value="application/pdf">PDF</option>
      <option value="image/png">PNG</option>
      <option value="text/html">HTML</option>
      <option value="text/plain">TXT</option>
    </select>
    <br><br>
    <input name="submit" type="submit" value="Convert">
  </form>
</body>
</html>
