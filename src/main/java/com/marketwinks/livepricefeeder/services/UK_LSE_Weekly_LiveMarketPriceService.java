package com.marketwinks.livepricefeeder.services;

import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.marketwinks.livepricefeeder.model.uk_lse_weekly_livemarketmacd;
import com.marketwinks.livepricefeeder.model.uk_lse_weekly_livemarketprice;
import com.marketwinks.livepricefeeder.repository.UK_LSE_Weekly_LiveMarketMacdRepository;
import com.marketwinks.livepricefeeder.repository.UK_LSE_Weekly_LiveMarketPriceRepository;

@RestController
@RequestMapping("/uk_lse_weekly_livemarketprice")
public class UK_LSE_Weekly_LiveMarketPriceService {

	@Autowired
	private UK_LSE_Weekly_LiveMarketPriceRepository UK_LSE_Weekly_LiveMarketPriceRepository;

	@Autowired
	private UK_LSE_Weekly_LiveMarketMacdRepository UK_LSE_Weekly_LiveMarketMacdRepository;

	@org.springframework.scheduling.annotation.Async
	@RequestMapping(value = "/Weekly/{symbol}", method = RequestMethod.GET)
	public boolean UK_LSE_Weekly_LiveMarketPrice5MinsParser(@PathVariable String symbol) {

		boolean execution_result = false;

		String feedURLFull = "https://uk.finance.yahoo.com/quote/" + symbol + ".L";

		HttpGet request = null;
		String url = feedURLFull;
		String content = null;
		String price = null;

		try {

			HttpClient client = HttpClientBuilder.create().build();
			request = new HttpGet(url);

			request.addHeader("User-Agent", "Apache HTTPClient");
			HttpResponse response = null;
			try {
				response = client.execute(request);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			HttpEntity entity = response.getEntity();
			try {
				content = EntityUtils.toString(entity);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String splitstring = "\"quoteType\":\"EQUITY\",\"invalid\":false,\"symbol\":\"" + symbol + ".L" + "\"";
			String[] split = content.split(splitstring);
			String resultString = null;
			String resultString2 = null;
			int index;

			resultString = split[0].substring(split[0].length() - 600);
			String[] split2 = resultString.split(",\"regularMarketPrice\":");

			resultString2 = split2[1].substring(7, 17);

			index = resultString2.indexOf(',');

			price = resultString2.substring(0, index);

			System.out.println("Hey here is the price");
			System.out.println(price);

		} finally {

			if (request != null) {

				request.releaseConnection();
			}
		}

		// save to the db
		uk_lse_weekly_livemarketprice uk_lse_weekly_livemarketprice = new uk_lse_weekly_livemarketprice();
		uk_lse_weekly_livemarketprice.setPrice(price);
		uk_lse_weekly_livemarketprice.setSymbol(symbol);
		java.time.LocalDateTime time = LocalDateTime.now();
		uk_lse_weekly_livemarketprice.setTime(time);

		uk_lse_weekly_livemarketprice result = UK_LSE_Weekly_LiveMarketPriceRepository.save(uk_lse_weekly_livemarketprice);

		uk_lse_weekly_livemarketmacd uk_lse_weekly_livemarketmacd = new uk_lse_weekly_livemarketmacd();
		uk_lse_weekly_livemarketmacd.setPrice(price);
		uk_lse_weekly_livemarketmacd.setSymbol(symbol);
		uk_lse_weekly_livemarketmacd.setTime(time);

		uk_lse_weekly_livemarketmacd.setEma12("calculating");

		uk_lse_weekly_livemarketmacd.setEma26("calculating");
		uk_lse_weekly_livemarketmacd.setSignal("calculating");
		uk_lse_weekly_livemarketmacd.setMacd("calculating");
		uk_lse_weekly_livemarketmacd.setHistogram("calculating");

		uk_lse_weekly_livemarketmacd resultOfMarketFeedMACD = UK_LSE_Weekly_LiveMarketMacdRepository
				.save(uk_lse_weekly_livemarketmacd);

		execution_result = true;
		return execution_result;

	}

}
