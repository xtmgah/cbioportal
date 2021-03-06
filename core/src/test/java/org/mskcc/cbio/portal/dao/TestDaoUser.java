/*
 * Copyright (c) 2015 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.mskcc.cbio.portal.dao;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.mskcc.cbio.portal.model.User;
import org.mskcc.cbio.portal.dao.DaoUser;
import org.mskcc.cbio.portal.scripts.ResetDatabase;

/**
 * JUnit test for DaoUser class.
 */
public class TestDaoUser extends TestCase {

   public void testDaoUser() throws Exception {
      ResetDatabase.resetDatabase();

      User user = new User("joe@mail.com", "Joe Smith", false);
      DaoUser.addUser(user);

      assertEquals(null, DaoUser.getUserByEmail("foo"));
      assertEquals(user, DaoUser.getUserByEmail("joe@mail.com"));
      assertFalse(user.isEnabled());

      User user2 = new User("jane@yahoo.com", "Jane Doe", false);
      DaoUser.addUser(user2);

      ArrayList<User> allUsers = DaoUser.getAllUsers();
      assertEquals(2, allUsers.size());

      DaoUser.deleteUser(user.getEmail());
      allUsers = DaoUser.getAllUsers();
      assertEquals(user2, allUsers.get(0));

      assertEquals(user2, DaoUser.getUserByEmail("jane@yahoo.com"));
   }
}
