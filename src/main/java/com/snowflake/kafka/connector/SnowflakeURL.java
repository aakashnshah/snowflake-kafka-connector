/*
 * Copyright (c) 2019 Snowflake Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.snowflake.kafka.connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SnowflakeURL
{

  private String jdbcUrl;

  private String url;

  private boolean ssl;

  private String account;

  private int port;

  private static final Logger LOGGER =
    LoggerFactory.getLogger(SnowflakeURL.class.getName());

  SnowflakeURL(String urlStr)
  {
    Pattern pattern = Pattern.compile("^(https?://)?((([\\w\\d]+)(\\" +
      ".[\\w\\d-]+){2,})(:(\\d+))?)/?$");

    Matcher matcher = pattern.matcher(urlStr.trim().toLowerCase());

    if (!matcher.find())
    {
      LOGGER.error("invalid Snowflake URL {}", urlStr);

      throw new IllegalArgumentException("invalid Snowflake URL " + urlStr);
    }

    if ("http://".equals(matcher.group(1)))
    {
      ssl = false;
    }
    else
    {
      ssl = true;
    }

    url = matcher.group(3);

    account = matcher.group(4);

    if (matcher.group(7) != null)
    {
      port = Integer.parseInt(matcher.group(7));
    }
    else if (ssl)
    {
      port = 443;
    }
    else
    {
      port = 80;
    }

    jdbcUrl = "jdbc:snowflake://" + url + ":" + port;

    LOGGER.debug("parsed Snowflake URL: " + urlStr);

  }

  String getJdbcUrl()
  {
    return jdbcUrl;
  }

  String getAccount()
  {
    return account;
  }

  boolean sslEnabled()
  {
    return ssl;
  }

  String getScheme()
  {
    if (ssl)
    {
      return "https";
    }
    else
    {
      return "http";
    }
  }

  String getFullUrl()
  {
    return url + ":" + port;
  }

  String getUrlWithoutPort()
  {
    return url;
  }

  int getPort()
  {
    return port;
  }

  @Override
  public String toString()
  {
    return getFullUrl();
  }
}
