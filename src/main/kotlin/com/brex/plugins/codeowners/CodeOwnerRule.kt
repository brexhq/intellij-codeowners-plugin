package com.brex.plugins.codeowners

/** Represents a single Code Owner rule */
data class CodeOwnerRule(
    /** A pattern describing the files to match */
    val pattern: String,
    /** The list of owners of the matched files */
    val owners: List<String>,
    /** The line number of this rule in its CODEOWNERS file */
    val lineNumber: Int
) {
    companion object {
        /** Parse a CodeOwnerRule from a CODEOWNERS line */
        fun fromCodeOwnerLine(lineNumber: Int, line: List<String>) =
            CodeOwnerRule(line[0], line.drop(1), lineNumber)
    }
}
