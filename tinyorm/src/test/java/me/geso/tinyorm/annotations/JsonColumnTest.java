package me.geso.tinyorm.annotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import lombok.Getter;
import lombok.Setter;
import me.geso.jdbcutils.RichSQLException;
import me.geso.tinyorm.Row;
import me.geso.tinyorm.TestBase;

public class JsonColumnTest extends TestBase {

	@Test
	public void test() throws SQLException, RichSQLException {
		createTable("x",
			"id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT",
			"propertiesDump LONGBLOB NOT NULL");

		Map<String, String> map = new HashMap<>();
		map.put("hoge", "fuga");
		X created = orm.insert(X.class)
			.value("propertiesDump", map)
			.executeSelect();
		created = created.refetch().get();
		assertEquals("fuga", created.getPropertiesDump().get("hoge"));

		ResultSet rs = orm.getConnection()
			.prepareStatement(
				"SELECT propertiesDump FROM x")
			.executeQuery();
		assertTrue(rs.next());
		assertEquals("{\"hoge\":\"fuga\"}", new String(rs.getBytes(1), Charset.forName("UTF-8")));
		assertFalse(rs.next());
	}

	@Getter
	@Setter
	@Table("x")
	public static class X extends Row<X> {
		@PrimaryKey
		private long id;

		@JsonColumn
		private Map<String, String> propertiesDump;
	}

}
