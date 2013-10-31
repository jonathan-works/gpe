namespace("infox.TurnoCalendar",
function TurnoCalendar() {
	var selectedRow = null;
	var selectedCol = null;
	
	function removeChecked(row, col) {
		console.log("remove",row,col);
		getCheckbox(row, col).checked = false;
		getCell(row, col).removeClass('fixed-selected');
	}
	
	function removeAllSelected() {
		jQuery('.selected').removeClass('selected');
	}
	
	function getCell(row, col) {
		return jQuery('#td_' + row + "_" + col);
	}
	
	function getCheckbox(row, col) {
		return jQuery("input[id$='check_" + row + "_" + col + "']")[0];
	}
	
	function setClassInInterval(col, fromRow, toRow, classe) {
		if (fromRow > toRow) {
			var aux = fromRow;
			fromRow = toRow;
			toRow = aux;
		}
		for (var row = fromRow; row <= toRow; row++) {
			getCell(row, col).addClass(classe);
		}
	}
	
	function setCheckedInInterval(col, fromRow, toRow, checked) {
		if (fromRow > toRow) {
			var aux = fromRow;
			fromRow = toRow;
			toRow = aux;
		}
		for (var row = fromRow; row <= toRow; row++) {
			getCheckbox(row, col).checked = checked;
		}
	}
	
	var result = {
		markBegin : function(row, col) {
			selectedRow = row;
			selectedCol = col;
			getCell(row, col).addClass('selected');
		},
		markInterval : function(row, col) {
			removeAllSelected();
			
			if (selectedRow != null && selectedCol == col) {
				setClassInInterval(col, selectedRow, row, 'selected');
			}
		},
		markEnd : function(row, col) {
			removeAllSelected();
			
			if (selectedCol == col) {
				setClassInInterval(col, selectedRow, row, 'fixed-selected');
				setCheckedInInterval(col, selectedRow, row, true);
				selectedCol = selectedRow = null;
			} else {
				this.markBegin(row, col);
			}
		},
		removeInterval : function(row, col) {
			removeChecked(row, col);
			
			var i = row - 1;
			while (i >= 0 && getCell(i, col).hasClass('fixed-selected')) {
				removeChecked(i, col);
				i--;
			}
			
			i = row + 1;
			while (i <= 23 && getCell(i, col).hasClass('fixed-selected')) {
				removeChecked(i, col);
				i++;
			}
		},
		isSelected : function(row, col) {
			return getCell(row, col).hasClass('fixed-selected');
		},
		isBeginMarked : function() {
			return selectedCol != null && selectedRow != null;
		},
		toggleMark : function(row, col) {
			if (!turnoCalendar.isBeginMarked()) {
    			if (turnoCalendar.isSelected(row, col)) {
    				turnoCalendar.removeInterval(row, col);
    			} else {
    				turnoCalendar.markBegin(row, col);
    			}
    		} else {
    			turnoCalendar.markEnd(row,col);
    		}
		},
		moveIntervalMark:function(row,col) {
			if (turnoCalendar.isBeginMarked()) {
    			turnoCalendar.markInterval(row, col);
    		}
		}
	}
	return result;
});