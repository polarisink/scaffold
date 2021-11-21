package com.lqs.scaffold.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * @author lqs
 * @describe
 * @date 2021/11/6
 */
@Slf4j
public class LangUtil {

	public static String preHandleLang(String language) {
		return language.toLowerCase().replaceAll("-", "_");
	}

	public static Locale getLocale(String lang) {
		if (!lang.contains("_")) {
			return new Locale(lang);
		}
		String[] parts = lang.split("_");

		Locale locale;
		try {
			locale = new Locale(parts[0], parts[1].toUpperCase());
		} catch (Throwable e) {
			log.warn(String.format("parse lang: %s error", lang), e);
			locale = LocaleContextHolder.getLocale();
		}
		return locale;
	}

}
