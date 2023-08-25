//                                                                            //
// My Bookmaker - Markdown-based creation of printed books                    //
// Copyright (C) 2023 Mirko Raner                                             //
//                                                                            //
// This program is free software: you can redistribute it and/or modify       //
// it under the terms of the GNU Affero General Public License as             //
// published by the Free Software Foundation, either version 3 of the         //
// License, or (at your option) any later version.                            //
//                                                                            //
// This program is distributed in the hope that it will be useful,            //
// but WITHOUT ANY WARRANTY; without even the implied warranty of             //
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the               //
// GNU Affero General Public License for more details.                        //
//                                                                            //
// You should have received a copy of the GNU Affero General Public License   //
// along with this program. If not, see <https://www.gnu.org/licenses/>.      //
//                                                                            //
package my.bookmaker.source

import java.io.InputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path

class FileSystemSource(private val fileSystemLoader: FileSystemLoader, override val path: String): Source {
    constructor(cwd: Path, path: String): this(FileSystemLoader(cwd), path)
    override val inputStream: InputStream get() = Files.newInputStream(fileSystemLoader.cwd.resolve(path))
    override val url: URL get() = fileSystemLoader.cwd.resolve(path).toUri().toURL()
    override val loader: Loader get() = fileSystemLoader
}
