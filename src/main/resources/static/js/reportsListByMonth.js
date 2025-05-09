const select = document.getElementById('selectDate');
select.addEventListener('change', (e) => {

    let listCount = 0;

    if (e.target.value === '-1') {
        var selectedDate = convertDateFormat(e.target.value);
        var rows = document.querySelectorAll("#myTable tbody tr");

        for (let i = 0; i < rows.length; i++) {
            rows[i].style.display = '';
        }
        listCount = rows.length;

    } else {
        var selectedDate = convertDateFormat(e.target.value);
        var rows = document.querySelectorAll("#myTable tbody tr");

        for (let i = 0; i < rows.length; i++) {
            let cells = rows[i].getElementsByTagName('td');
            if (cells[1].innerText === selectedDate) {
                rows[i].style.display = '';
                listCount++;
            } else {
                rows[i].style.display = 'none';
            }
        }
    }
    document.getElementById('listSize').textContent = "（ 全" + listCount + "件 ）";
});

function convertDateFormat(dateString) {
    // YYYY-MM-DD形式の日付を分割
    var parts = dateString.split("-");
    var year = parts[0];
    var month = parts[1];

    // YYYY/MM形式に変換
    return year + "/" + month;
}