package ch.eaternity.shared;

/**
 * In future to be expanded with finer grained season types (starting season, high season, ending season, off season)
 * 
 */
public enum SeasonType {

	// DISCUSS Aurelian: we need also the information that a product doens't have season dependencies. best way here?
	// DISCUSS Might be good enough to define MAIN_SEASON over the whole year? What is the difference between
	// OUT_OF_SEASON and NO_SEASON? Does NO_SEASON mean that it is always in season?
	MAIN_SEASON, OUT_OF_SEASON, NO_SEASON

}
