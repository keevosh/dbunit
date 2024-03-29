/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2004, DbUnit.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.dbunit.dataset.csv;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.dbunit.dataset.common.handlers.IllegalInputCharacterException;
import org.dbunit.dataset.common.handlers.PipelineException;

/**
 * @author fede
 * @author Last changed by: $Author: gommma $
 * @version $Revision: 825 $ $Date: 2008-10-05 12:40:31 -0500 (Sun, 05 Oct 2008) $
 * @since 2.2 (Sep 12, 2004)
 */
public interface CsvParser {
    List parse(File file) throws IOException, CsvParserException;
    List parse(URL url) throws IOException, CsvParserException;
    List parse(String csv) throws PipelineException, IllegalInputCharacterException;
}
