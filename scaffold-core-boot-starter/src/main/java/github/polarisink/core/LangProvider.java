package github.polarisink.core;

import github.polarisink.util.LangUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * Language Provider
 *
 * @author Bill
 * @version 1.0
 * @since 2020-08-31
 */
@Slf4j
public class LangProvider {
	private static final String PATH_PARENT = "classpath:i18n/";
	private static final String SUFFIX = ".properties";
	private static final String DEFAULT_LANG = "en_us";
	/**
	 * volatile修饰对象或数组时,内部修改不能保证内存可见性,但此处对象属性全部使用final修饰,不用担心.但是会不能使用外部配置，暂时不知道如何解决
	 */
	protected static volatile LangProvider instance;
	private final MessageSourceAccessor accessor;
	private final MessageSourceAccessor defaultAccessor;
	private final String lang;

	private LangProvider(String language) throws IOException {
		lang = LangUtil.preHandleLang(language);
		accessor = getMessageSourceAccessor(lang);
		defaultAccessor = getMessageSourceAccessor(DEFAULT_LANG);
	}

	public static LangProvider getInstance(String language) throws IOException {
		if (instance == null) {
			synchronized (LangProvider.class) {
				if (instance == null) {
					instance = new LangProvider(language);
				}
			}
		}
		return instance;
	}

	private MessageSourceAccessor getMessageSourceAccessor(String lang) throws IOException {
		Resource resource = new PathMatchingResourcePatternResolver().getResource(
			PATH_PARENT + lang + SUFFIX);
		String fileName = resource.getURL().toString();
		int lastIndex = fileName.lastIndexOf(".");
		String baseName = fileName.substring(0, lastIndex);
		ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource
			= new ReloadableResourceBundleMessageSource();
		reloadableResourceBundleMessageSource.setBasename(baseName);
		reloadableResourceBundleMessageSource.setCacheSeconds(5);
		reloadableResourceBundleMessageSource.setDefaultEncoding("UTF-8");
		return new MessageSourceAccessor(reloadableResourceBundleMessageSource);
	}

	public String getMessage(String label) {
		Locale locale = LangUtil.getLocale(lang);
		String message = accessor.getMessage(label, "", locale);
		if (isBlank(message)) {
			message = defaultAccessor.getMessage(label, "", locale);
		}
		return message;
	}

}
