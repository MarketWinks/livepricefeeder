package com.marketwinks.livepricefeeder.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.marketwinks.livepricefeeder.model.uk_lse_5mins_livemarketmacd;
import com.marketwinks.livepricefeeder.repository.UK_LSE_5Mins_LiveMarketMacdRepository;;

@RestController
@RequestMapping("/uk_lse_5mins_livemarketmacd")
public class UK_LSE_5Mins_LiveMarketMacdService {

	@Autowired
	private UK_LSE_5Mins_LiveMarketMacdRepository UK_LSE_5Mins_LiveMarketMacdRepository;

	@org.springframework.scheduling.annotation.Async
	@RequestMapping(value = "/calc/{symbol}", method = RequestMethod.GET)
	public boolean UK_LSE_5Mins_LiveMarketMacdParser(@PathVariable String symbol) {

		boolean execution_result = false;
		int calcStartindex = 0;
		int MarketFeedsSizeForSymbol = 0;

		List<uk_lse_5mins_livemarketmacd> MarketFeeds_full = UK_LSE_5Mins_LiveMarketMacdRepository.findAll();

		try {

			System.out.println("MACD Calculation started for:" + symbol);

			// List<uk_lse_5mins_livemarketmacd> MarketFeeds_full =
			// UK_LSE_5Mins_LiveMarketMacdRepository.findAll();

			List<uk_lse_5mins_livemarketmacd> MarketFeeds = new ArrayList<uk_lse_5mins_livemarketmacd>();

			for (int i = 0; i < MarketFeeds_full.size(); i++) {

				if (MarketFeeds_full.get(i).getSymbol().equals(symbol)) {
					MarketFeedsSizeForSymbol++;
				}

			}

			for (int i = 0; i < MarketFeedsSizeForSymbol; i++) {
				// MarketFeeds_full.clear();
				// MarketFeeds_full = UK_LSE_5Mins_LiveMarketMacdRepository.findAll();
				MarketFeeds.clear();
				for (int j = 0; j < MarketFeeds_full.size(); j++) {

					if (MarketFeeds_full.get(j).getSymbol().equals(symbol)) {
						MarketFeeds.add(MarketFeeds_full.get(j));
					}

				}

				Collections.sort(MarketFeeds, new SortbyLatestTime());

				// for (int x = 0; x < MarketFeedsSizeForSymbol; x++) {
				// System.out.println(MarketFeeds.get(x).getTime());
				// }

				for (int index = 0; index < MarketFeedsSizeForSymbol; index++) {
					if (MarketFeeds.get(index).getEma12().toString().equals("calculating")
							|| MarketFeeds.get(index).getEma26().toString().equals("calculating")
							|| MarketFeeds.get(index).getHistogram().toString().equals("calculating")
							|| MarketFeeds.get(index).getMacd().toString().equals("calculating")
							|| MarketFeeds.get(index).getSignal().toString().equals("calculating")) {
						calcStartindex = index;
						System.out.println("The calcStartindex is: " + calcStartindex + " for: " + symbol);

						String latestEma12 = MarketFeeds.get(calcStartindex - 1).getEma12();
						String latestEma26 = MarketFeeds.get(calcStartindex - 1).getEma26();
						String latestSignal = MarketFeeds.get(calcStartindex - 1).getSignal();
						String latestMacd = MarketFeeds.get(calcStartindex - 1).getMacd();
						String latestHistogram = MarketFeeds.get(calcStartindex - 1).getHistogram();
						LocalDateTime latestTime = MarketFeeds.get(calcStartindex - 1).getTime();

						String price = MarketFeeds.get(calcStartindex).getPrice();

						// How to Calculate MACD-
						// http:///investexcel.net/how-to-calculate-macd-in-excel/

						String ema12 = null;
						String ema26 = null;
						String macd = null;
						String signal = null;
						String histogram = null;
						ema12 = Double.toString(
								Double.parseDouble(price) / 6.5 + Double.parseDouble(latestEma12) * 5.5 / 6.5);

						ema26 = Double.toString(
								Double.parseDouble(price) / 13.5 + Double.parseDouble(latestEma26) * 12.5 / 13.5);

						macd = Double.toString(Double.parseDouble(ema12) - Double.parseDouble(ema26));

						signal = Double
								.toString(Double.parseDouble(macd) / 5 + Double.parseDouble(latestSignal) * 4 / 5);

						histogram = Double.toString(Double.parseDouble(macd) - Double.parseDouble(signal));

						// save to the db

						uk_lse_5mins_livemarketmacd MarketFeed = new uk_lse_5mins_livemarketmacd();
						MarketFeed.setPrice(price);
						MarketFeed.setSymbol(symbol);
						MarketFeed.setTime(MarketFeeds.get(calcStartindex).getTime());

						MarketFeed.setEma12(ema12);

						MarketFeed.setEma26(ema26);
						MarketFeed.setSignal(signal);
						MarketFeed.setMacd(macd);
						MarketFeed.setHistogram(histogram);

						uk_lse_5mins_livemarketmacd result = UK_LSE_5Mins_LiveMarketMacdRepository.save(MarketFeed);

						System.out.println("MACD Calc completed for symbol:" + symbol);

						// local in-memory MarketFeeds_full updated for this record

						for (int u = 0; u < MarketFeeds_full.size(); u++) {

							if (MarketFeeds_full.get(u).get_id().equals(MarketFeeds.get(calcStartindex).get_id())) {
								MarketFeeds_full.get(u).setEma12(ema12);
								MarketFeeds_full.get(u).setEma26(ema26);
								MarketFeeds_full.get(u).setSignal(signal);
								MarketFeeds_full.get(u).setMacd(macd);
								MarketFeeds_full.get(u).setHistogram(histogram);

								MarketFeeds.add(MarketFeeds_full.get(u));
							}

						}

						// attempt to delete the duplicate record in table

						UK_LSE_5Mins_LiveMarketMacdRepository.deleteById(MarketFeeds.get(calcStartindex).get_id());
						System.out.println("Duplicate record delete attempted");

						break;
					}
				}

			}

			execution_result = true;
		} catch (Exception e) {

			System.out.println(e);
		}

		return execution_result;

	}

	// @RequestMapping(value =
	// "/UK_LSE_5Mins_LiveMarketMacdForAnyTime/update/{symbol}/{price}/{ema12}/{ema26}/{signal}/{macd}/{histogram}",
	// method = RequestMethod.GET)
	// public boolean LiveMarketMacdForAnyTimeParser(@PathVariable String symbol,
	// @PathVariable String price,
	// @PathVariable String ema12, @PathVariable String ema26, @PathVariable String
	// signal,
	// @PathVariable String macd, @PathVariable String histogram) {
	//
	// boolean execution_result = false;
	//
	// // save to the db
	// uk_lse_5mins_livemarketmacd MarketFeed = new uk_lse_5mins_livemarketmacd();
	// MarketFeed.setPrice(price);
	// MarketFeed.setSymbol(symbol);
	// MarketFeed.setTime(LocalDateTime.now());
	//
	// // need to write macd calc logic to populate these values
	// MarketFeed.setEma12(ema12);
	// MarketFeed.setEma26(ema26);
	// MarketFeed.setSignal(signal);
	// MarketFeed.setMacd(macd);
	// MarketFeed.setHistogram(histogram);
	//
	// uk_lse_5mins_livemarketmacd result =
	// UK_LSE_5Mins_LiveMarketMacdRepository.save(MarketFeed);
	// execution_result = true;
	// return execution_result;
	//
	// }

	@RequestMapping(value = "/UK_LSE_5Mins_LiveMarketMacdUpdateByObjectId/update/{objectId}/{ema12}/{ema26}/{signal}/{macd}/{histogram}", method = RequestMethod.GET)
	public boolean LiveMarketMacdForAnyTimeParser(@PathVariable String objectId, @PathVariable String ema12,
			@PathVariable String ema26, @PathVariable String signal, @PathVariable String macd,
			@PathVariable String histogram) {

		boolean execution_result = false;

		List<uk_lse_5mins_livemarketmacd> MarketFeeds_full = UK_LSE_5Mins_LiveMarketMacdRepository.findAll();

		for (int u = 0; u < MarketFeeds_full.size(); u++) {

			if (MarketFeeds_full.get(u).get_id().equals(objectId)) {
				System.out.println("Record Found");

				uk_lse_5mins_livemarketmacd MarketFeed = new uk_lse_5mins_livemarketmacd();
				MarketFeed.setPrice(MarketFeeds_full.get(u).getPrice());
				MarketFeed.setSymbol(MarketFeeds_full.get(u).getSymbol());
				MarketFeed.setTime(MarketFeeds_full.get(u).getTime());

				MarketFeed.setEma12(ema12);
				MarketFeed.setEma26(ema26);
				MarketFeed.setSignal(signal);
				MarketFeed.setMacd(macd);
				MarketFeed.setHistogram(histogram);

				uk_lse_5mins_livemarketmacd result = UK_LSE_5Mins_LiveMarketMacdRepository.save(MarketFeed);

				System.out.println("New record with macd data inserted");

				// attempt to delete
				UK_LSE_5Mins_LiveMarketMacdRepository.deleteById(objectId);
				System.out.println("Duplicate record delete attempted");

				execution_result = true;

			}

		}
		
		MarketFeeds_full.clear();

		return execution_result;

	}
	
	//HAVE TO WRITE A BLOCK TO RESET TO CALCULATING...
	
	

	// @RequestMapping(value = "/detectRecordByObjectId/{objectId}", method =
	// RequestMethod.GET)
	// public boolean LiveMarketMacdForAnyTimeParser(@PathVariable String objectId)
	// {
	//
	// boolean execution_result = false;
	//
	// List<uk_lse_5mins_livemarketmacd> MarketFeeds_full =
	// UK_LSE_5Mins_LiveMarketMacdRepository.findAll();
	//
	// for (int u = 0; u < MarketFeeds_full.size(); u++) {
	//
	// System.out.println(MarketFeeds_full.get(u).get_id());
	//
	// if (MarketFeeds_full.get(u).get_id().equals(objectId)) {
	// System.out.println("Record Found");
	//
	// System.out.println(MarketFeeds_full.get(u).getPrice() + "," +
	//
	// MarketFeeds_full.get(u).getSymbol() + "," +
	//
	// MarketFeeds_full.get(u).getTime().toString() + "," +
	//
	// MarketFeeds_full.get(u).getEma12() + "," + MarketFeeds_full.get(u).getEma26()
	// + ","
	// + MarketFeeds_full.get(u).getSignal() + "," +
	// MarketFeeds_full.get(u).getMacd() + ","
	// + MarketFeeds_full.get(u).getHistogram());
	//
	// execution_result = true;
	//
	// }
	//
	// }
	//
	// return execution_result;
	//
	// }

}

class SortbyLatestTime implements Comparator<uk_lse_5mins_livemarketmacd> {
	// Used for sorting in ascending order of
	// roll number
	public int compare(uk_lse_5mins_livemarketmacd a, uk_lse_5mins_livemarketmacd b) {
		return a.getTime().compareTo(b.getTime());
	}
}
