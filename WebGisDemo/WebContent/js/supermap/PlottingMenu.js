//态势标绘
function addMenuPlotting() {
	$(document).ready(function() {
		$('#menuPlotting').sticklr({
			showOn : 'hover',
			stickTo : 'left'
		});
	});

	plottingLayer.style = {
		fillColor : "#66cccc",
		fillOpacity : 0.4,
		strokeColor : "#66cccc",
		strokeOpacity : 1,
		strokeWidth : 3,
		pointRadius : 6
	};
	var stylePoint = {
		fillColor : "#323232",
		fillOpacity : 0.4,
		strokeColor : "#323232",
		strokeOpacity : 0.8,
		strokeWidth : 15,
		strokeDashstyle : "dash"
	};
	var styleMultiPoint = {
		fillColor : "#323232",
		fillOpacity : 1,
		strokeColor : "#323232",
		strokeOpacity : 1,
		strokeWidth : 1,
		pointRadius : 8
	};
	var styleFlag = {
		fillColor : "#EA2D1B",
		fillOpacity : 1,
		strokeColor : "#EA2D1B",
		strokeOpacity : 1,
		strokeWidth : 2
	};

	// 态势标绘编辑
	plottingEdit = new SuperMap.Control.PlottingEdit(plottingLayer);

	// 点标
	drawGeoPoint = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.PointEx, {
				style : stylePoint
			});
	drawGeoMultiPoint = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.MultiPointEx, {
				style : styleMultiPoint
			});
	// 线标
	drawGeoPolyline = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.PolyLineEx);
	drawGeoArc = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.ArcEx);
	drawGeoFreeline = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.FreelineEx);
	drawGeoBizer2 = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.BezierCurve2Ex);
	drawGeoBizer3 = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.BezierCurve3Ex);
	drawGeoBizerN = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.BezierCurveNEx);
	drawGeoCardinalCurve = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.CardinalCurveEx);
	// 面标
	drawGeoCircle = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.CircleEx);
	drawGeoEllipse = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.EllipseEx);
	drawGeoSector = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.Sector);
	drawGeoLune = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.Lune);
	drawGeoRectangle = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.Rectangle);
	drawGeoPolygon = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.PolygonEx);
	drawGeoFreePolygon = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.FreePolygon);
	drawGeoGatheringPlace = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.GatheringPlace);
	drawGeoRoundedRect = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.RoundedRect);
	drawGeoCloseCurve = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.CloseCurve);
	//旗标
	drawGeoCurveFlag = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.CurveFlag, {
				style : styleFlag
			});
	drawGeoRectFlag = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.RectFlag, {
				style : styleFlag
			});
	drawGeoTriangleFlag = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.TriangleFlag, {
				style : styleFlag
			});
	//面状箭标
	drawGeoStraightArrow = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.StraightArrow);
	drawGeoDiagonalArrow = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.DiagonalArrow);
	drawGeoDoubleArrow = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.DoubleArrow);
	drawGeoDoveTailStraightArrow = new SuperMap.Control.DrawFeature(
			plottingLayer, SuperMap.Handler.DoveTailStraightArrow);
	drawGeoDoveTailDiagonalArrow = new SuperMap.Control.DrawFeature(
			plottingLayer, SuperMap.Handler.DoveTailDiagonalArrow);
	
	// 线状箭标
	drawGeoPolylineArrow = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.PolylineArrow);
	drawGeoParallelSearch = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.ParallelSearch);
	drawGeoSectorSearch = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.SectorSearch);
	drawGeoBezierCurveArrow = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.BezierCurveArrow);
	drawGeoCardinalCurveArrow = new SuperMap.Control.DrawFeature(plottingLayer,
			SuperMap.Handler.CardinalCurveArrow);
	// 添加态势标绘控件
	window.map.addControls([ plottingEdit, drawGeoPoint, drawGeoMultiPoint,
			drawGeoPolyline, drawGeoArc, drawGeoFreeline, drawGeoBizer2,
			drawGeoBizer3, drawGeoBizerN, drawGeoCardinalCurve, drawGeoCircle,
			drawGeoEllipse, drawGeoSector, drawGeoLune, drawGeoRectangle,
			drawGeoPolygon, drawGeoFreePolygon, drawGeoGatheringPlace,
			drawGeoRoundedRect, drawGeoCloseCurve, drawGeoCurveFlag,
			drawGeoRectFlag, drawGeoTriangleFlag, drawGeoStraightArrow,
			drawGeoDiagonalArrow, drawGeoDoubleArrow,
			drawGeoDoveTailStraightArrow, drawGeoDoveTailDiagonalArrow,
			drawGeoPolylineArrow, drawGeoParallelSearch, drawGeoSectorSearch,
			drawGeoBezierCurveArrow, drawGeoCardinalCurveArrow ]);
}

//取消标绘与编辑
function plottingAllDeactivate() {
	plottingEdit.deactivate();
	drawGeoPoint.deactivate();
	drawGeoMultiPoint.deactivate();
	drawGeoPolyline.deactivate();
	drawGeoArc.deactivate();
	drawGeoFreeline.deactivate();
	drawGeoBizer2.deactivate();
	drawGeoBizer3.deactivate();
	drawGeoBizerN.deactivate();
	drawGeoCardinalCurve.deactivate();
	drawGeoCircle.deactivate();
	drawGeoEllipse.deactivate();
	drawGeoSector.deactivate();
	drawGeoLune.deactivate();
	drawGeoRectangle.deactivate();
	drawGeoPolygon.deactivate();
	drawGeoFreePolygon.deactivate();
	drawGeoGatheringPlace.deactivate();
	drawGeoRoundedRect.deactivate();
	drawGeoCloseCurve.deactivate();
	drawGeoCurveFlag.deactivate();
	drawGeoRectFlag.deactivate();
	drawGeoTriangleFlag.deactivate();
	drawGeoStraightArrow.deactivate();
	drawGeoDiagonalArrow.deactivate();
	drawGeoDoubleArrow.deactivate();
	drawGeoDoveTailStraightArrow.deactivate();
	drawGeoDoveTailDiagonalArrow.deactivate();
	drawGeoPolylineArrow.deactivate();
	drawGeoParallelSearch.deactivate();
	drawGeoSectorSearch.deactivate();
	drawGeoBezierCurveArrow.deactivate();
	drawGeoCardinalCurveArrow.deactivate();
}

//点标
function plottingDrawGeoPoint() {
	plottingAllDeactivate();
	drawGeoPoint.activate();
}

function plottingDrawGeoMultiPoint() {
	plottingAllDeactivate();
	drawGeoMultiPoint.activate();
}

//线标
function plottingdrawGeoPolyline() {
	plottingAllDeactivate();
	drawGeoPolyline.activate();
}

function plottingDrawGeoArc() {
	plottingAllDeactivate();
	drawGeoArc.activate();
}

function plottingDrawGeoFreeline() {
	plottingAllDeactivate();
	drawGeoFreeline.activate();
}

function plottingDrawGeoBizer2() {
	plottingAllDeactivate();
	drawGeoBizer2.activate();
}

function plottingDrawGeoBizer3() {
	plottingAllDeactivate();
	drawGeoBizer3.activate();
}

function plottingDrawGeoBizerN() {
	plottingAllDeactivate();
	drawGeoBizerN.activate();
}

function plottingDrawGeoCardinalCurve() {
	plottingAllDeactivate();
	drawGeoCardinalCurve.activate();
}

function plottingdrawGeoCircle() {
	plottingAllDeactivate();
	drawGeoCircle.activate();
}

function plottingDrawGeoEllipse() {
	plottingAllDeactivate();
	drawGeoEllipse.activate();
}

function plottingDrawGeoSector() {
	plottingAllDeactivate();
	drawGeoSector.activate();
}

function plottingDrawGeoLune() {
	plottingAllDeactivate();
	drawGeoLune.activate();
}

function plottingDrawGeoRectangle() {
	plottingAllDeactivate();
	drawGeoRectangle.activate();
}

function plottingDrawGeoPolygon() {
	plottingAllDeactivate();
	drawGeoPolygon.activate();
}

function plottingDrawGeoFreePolygon() {
	plottingAllDeactivate();
	drawGeoFreePolygon.activate();
}
function plottingDrawGeoGatheringPlace() {
	plottingAllDeactivate();
	drawGeoGatheringPlace.activate();
}

function plottingDrawGeoRoundedRect() {
	plottingAllDeactivate();
	drawGeoRoundedRect.activate();
}

function plottingDrawGeoCloseCurve() {
	plottingAllDeactivate();
	drawGeoCloseCurve.activate();
}

//旗标
function plottingdrawGeoCurveFlag() {
	plottingAllDeactivate();
	drawGeoCurveFlag.activate();
}

function plottingDrawGeoRectFlag() {
	plottingAllDeactivate();
	drawGeoRectFlag.activate();
}

function plottingDrawGeoTriangleFlag() {
	plottingAllDeactivate();
	drawGeoTriangleFlag.activate();
}

//箭标
function plottingdrawGeoStraightArrow() {
	plottingAllDeactivate();
	drawGeoStraightArrow.activate();
}

function plottingDrawGeoDiagonalArrow() {
	plottingAllDeactivate();
	drawGeoDiagonalArrow.activate();
}

function plottingDrawGeoDoubleArrow() {
	plottingAllDeactivate();
	drawGeoDoubleArrow.activate();
}

function plottingDrawGeoDoveTailStraightArrow() {
	plottingAllDeactivate();
	drawGeoDoveTailStraightArrow.activate();
}

function plottingDrawGeoDoveTailDiagonalArrow() {
	plottingAllDeactivate();
	drawGeoDoveTailDiagonalArrow.activate();
}

function plottingdrawGeoPolylineArrow() {
	plottingAllDeactivate();
	drawGeoPolylineArrow.activate();
}

function plottingDrawGeoParallelSearch() {
	plottingAllDeactivate();
	drawGeoParallelSearch.activate();
}

function plottingDrawGeoSectorSearch() {
	plottingAllDeactivate();
	drawGeoSectorSearch.activate();
}

function plottingDrawGeoBezierCurveArrow() {
	plottingAllDeactivate();
	drawGeoBezierCurveArrow.activate();
}

function plottingDrawGeoCardinalCurveArrow() {
	plottingAllDeactivate();
	drawGeoCardinalCurveArrow.activate();
}

//清空绘制
function PlottingClear() {
	plottingAllDeactivate();
	plottingLayer.removeAllFeatures();
}

//取消标绘，激活标绘编辑控件
function PlottingDrawCancel() {
	plottingAllDeactivate();
	plottingEdit.activate();
}
