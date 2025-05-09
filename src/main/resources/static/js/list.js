document.getElementById('selectDate').addEventListener('change', function() {
    var selectedValue = this.value;
    document.getElementById('selectedDate').value = selectedValue;
    document.getElementById('dateForm').submit();
});