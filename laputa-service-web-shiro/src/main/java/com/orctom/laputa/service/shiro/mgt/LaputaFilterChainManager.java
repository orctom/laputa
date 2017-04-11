package com.orctom.laputa.service.shiro.mgt;

import com.orctom.laputa.service.filter.Filter;
import com.orctom.laputa.service.shiro.filter.PathMatchingFilter;
import com.orctom.laputa.service.shiro.filter.mgt.DefaultFilter;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.Nameable;
import org.apache.shiro.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class LaputaFilterChainManager {

  private Map<String, Filter> filters;

  private Map<String, NamedFilterList> filterChains;

  public LaputaFilterChainManager() {
    this.filters = new LinkedHashMap<>();
    this.filterChains = new LinkedHashMap<>();
    addDefaultFilters();
  }

  private void addDefaultFilters() {
    for (DefaultFilter defaultFilter : DefaultFilter.values()) {
      addFilter(defaultFilter.name(), defaultFilter.newInstance());
    }
  }

  public void addFilter(String name, Filter filter) {
    if (filter instanceof Nameable) {
      ((Nameable) filter).setName(name);
    }
    initFilter(filter);
    this.filters.put(name, filter);
  }

  private void initFilter(Filter filter) {

  }

  private Filter getFilter(String name) {
    return filters.get(name);
  }

  public Map<String, Filter> getFilters() {
    return filters;
  }

  public void createChain(String chainName, String chainDefinition) {
    if (!StringUtils.hasText(chainName)) {
      throw new NullPointerException("chainName cannot be null or empty.");
    }
    if (!StringUtils.hasText(chainDefinition)) {
      throw new NullPointerException("chainDefinition cannot be null or empty.");
    }
    String[] filterTokens = splitChainDefinition(chainDefinition);

    //each token is specific to each filter.
    //strip the name and extract any filter-specific config between brackets [ ]
    for (String token : filterTokens) {
      String[] nameConfigPair = toNameConfigPair(token);

      //now we have the filter name, path and (possibly null) path-specific config.  Let's apply them:
      addToChain(chainName, nameConfigPair[0], nameConfigPair[1]);
    }
  }

  protected String[] splitChainDefinition(String chainDefinition) {
    return StringUtils.split(
        chainDefinition,
        StringUtils.DEFAULT_DELIMITER_CHAR,
        '[',
        ']',
        true,
        true
    );
  }

  protected String[] toNameConfigPair(String token) throws ConfigurationException {

    try {
      String[] pair = token.split("\\[", 2);
      String name = StringUtils.clean(pair[0]);

      if (name == null) {
        throw new IllegalArgumentException("Filter name not found for filter chain definition token: " + token);
      }
      String config = null;

      if (pair.length == 2) {
        config = StringUtils.clean(pair[1]);
        //if there was an open bracket, it assumed there is a closing bracket, so strip it too:
        config = config.substring(0, config.length() - 1);
        config = StringUtils.clean(config);

        //backwards compatibility prior to implementing SHIRO-205:
        //prior to SHIRO-205 being implemented, it was common for end-users to quote the config inside brackets
        //if that config required commas.  We need to strip those quotes to get to the interior quoted definition
        //to ensure any existing quoted definitions still function for end users:
        if (config != null && config.startsWith("\"") && config.endsWith("\"")) {
          String stripped = config.substring(1, config.length() - 1);
          stripped = StringUtils.clean(stripped);

          //if the stripped value does not have any internal quotes, we can assume that the entire config was
          //quoted and we can use the stripped value.
          if (stripped != null && stripped.indexOf('"') == -1) {
            config = stripped;
          }
          //else:
          //the remaining config does have internal quotes, so we need to assume that each comma delimited
          //pair might be quoted, in which case we need the leading and trailing quotes that we stripped
          //So we ignore the stripped value.
        }
      }

      return new String[]{name, config};

    } catch (Exception e) {
      String msg = "Unable to parse filter chain definition token: " + token;
      throw new ConfigurationException(msg, e);
    }
  }

  public void addToChain(String chainName, String filterName) {
    addToChain(chainName, filterName, null);
  }

  public void addToChain(String chainName, String filterName, String chainSpecificFilterConfig) {
    if (!StringUtils.hasText(chainName)) {
      throw new IllegalArgumentException("chainName cannot be null or empty.");
    }
    Filter filter = getFilter(filterName);
    if (filter == null) {
      throw new IllegalArgumentException("There is no filter with name '" + filterName +
          "' to apply to chain [" + chainName + "] in the pool of available Filters.  Ensure a " +
          "filter with that name/path has first been registered with the addFilter method(s).");
    }

    applyChainConfig(chainName, filter, chainSpecificFilterConfig);

    NamedFilterList chain = ensureChain(chainName);
    chain.add(filter);
  }

  protected void applyChainConfig(String chainName, Filter filter, String chainSpecificFilterConfig) {
    if (filter instanceof PathMatchingFilter) {
      ((PathMatchingFilter) filter).processPathConfig(chainName, chainSpecificFilterConfig);
    } else {
      if (StringUtils.hasText(chainSpecificFilterConfig)) {
        //they specified a filter configuration, but the Filter doesn't implement PathConfigProcessor
        //this is an erroneous config:
        String msg = "chainSpecificFilterConfig was specified, but the underlying " +
            "Filter instance is not an 'instanceof' " +
            PathMatchingFilter.class.getName() + ".  This is required if the filter is to accept " +
            "chain-specific configuration.";
        throw new ConfigurationException(msg);
      }
    }
  }

  protected NamedFilterList ensureChain(String chainName) {
    NamedFilterList chain = getChain(chainName);
    if (chain == null) {
      chain = new NamedFilterList(chainName);
      this.filterChains.put(chainName, chain);
    }
    return chain;
  }

  public NamedFilterList getChain(String chainName) {
    return this.filterChains.get(chainName);
  }

  public boolean hasChains() {
    return !CollectionUtils.isEmpty(this.filterChains);
  }

  public Map<String, NamedFilterList> getFilterChains() {
    return filterChains;
  }
}
