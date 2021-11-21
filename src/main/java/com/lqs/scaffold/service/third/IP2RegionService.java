package com.lqs.scaffold.service.third;

import com.lqs.scaffold.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbMakerConfigException;
import org.lionsoul.ip2region.DbSearcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;

import static com.lqs.scaffold.constants.Regions.*;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * IP To Region Service
 *
 * @author lqs
 * @version 1.0
 * @since 2021-11-06
 */
@Slf4j
@Service
public class IP2RegionService {
	private final DbSearcher searcher;

	public IP2RegionService(
		@Value("${service.ip2region.store}") String store)
		throws BadRequestException {
		DbConfig config;
		try {
			config = new DbConfig();
		} catch (DbMakerConfigException e) {
			throw new BadRequestException(e);
		}
		try {
			searcher = new DbSearcher(config, store);
		} catch (FileNotFoundException e) {
			throw new BadRequestException(e);
		}
	}

	private DataBlock search(String ip) throws BadRequestException {
		try {
			long sTime = System.nanoTime();
			DataBlock dataBlock = searcher.memorySearch(ip);
			long cTime = (System.nanoTime() - sTime) / 1000000;
			log.info(String.format("%s in %d millseconds", dataBlock == null ? "EMPTY" : dataBlock.toString(), cTime));
			return dataBlock;
		} catch (IOException e) {
			throw new BadRequestException(e);
		}
	}

	public String getRegion(String ip) throws BadRequestException {
		ip = trimToNull(ip);
		if (ip == null) {
			return null;
		}
		if (ip.contains("::1")) {
			return CHINA;
		}
		DataBlock dataBlock = search(ip);
		return dataBlock == null ? null : getRegionHead(trimToEmpty(dataBlock.getRegion()));
	}

	private String getRegionHead(String region) {
		if (StringUtils.isBlank(region)) {
			return null;
		}
		int firstSegEndIndex = region.indexOf("|");
		if (firstSegEndIndex == -1) {
			return region;
		}
		return trimToEmpty(region.substring(0, firstSegEndIndex));
	}

}
