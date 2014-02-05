/**
 * Controller class for the Main Mutation view.
 * Listens to the various events and make necessary changes
 * on the view wrt each event type.
 *
 * @param mainMutationView  a MainMutationView instance
 * @param mutationDiagram   a MutationDiagram instance
 *
 * @author Selcuk Onur Sumer
 */
var MainMutationController = function (mainMutationView, mutationDiagram)
{
	function init()
	{
		// add listeners to the custom event dispatcher of the diagram

		mutationDiagram.dispatcher.on(
			MutationDetailsEvents.ALL_LOLLIPOPS_DESELECTED,
			allDeselectHandler);

		mutationDiagram.dispatcher.on(
			MutationDetailsEvents.LOLLIPOP_DESELECTED,
			deselectHandler);

		mutationDiagram.dispatcher.on(
			MutationDetailsEvents.LOLLIPOP_SELECTED,
			selectHandler);
	}

	function allDeselectHandler()
	{
		// hide filter reset info
		if (!mutationDiagram.isFiltered())
		{
			mainMutationView.hideFilterInfo();
		}
	}

	function deselectHandler(datum, index)
	{
		// hide filter reset info
		allDeselectHandler();
	}

	function selectHandler(datum, index)
	{
		// show filter reset info
		mainMutationView.showFilterInfo();
	}

	init();
};
